package lombok.core.configuration;

import java.util.ArrayList;
import java.util.List;

public final class MetaAnnotation implements ConfigurationValueType {
	private Annotation source;
	private List<Annotation> target;

	@Override
	public String toString() {
		return source + " => " + target;
	}

	public static MetaAnnotation valueOf(String value) {
		List<Annotation> annotations = MetaAnnotationParser.parse(value);

		if (annotations.size() < 2) {
			throw new IllegalArgumentException("At least one target annotation is required");
		}

		MetaAnnotation meta = new MetaAnnotation();
		meta.source = annotations.remove(0);
		meta.target = annotations;
		return meta;
	}

	public Annotation getSource() {
		return source;
	}

	public String getSourceName() {
		return source.name;
	}

	public List<Annotation> getTargets() {
		return target;
	}
	
	public static String description() {
		return "meta-annotation";
	}
	
	public static String exampleValue() {
		return "@fully.qualified.source @fully.qualified.target1(int=1, bound=<value|\"default\">) @fully.qualified.target2";
	}
	
	public static class Annotation {
		public String name;
		public List<Argument> args;
		public boolean replace;
		public boolean add;

		@Override
		public String toString() {
			return "@" + name + "(" + args + ")";
		}
	}

	public static class Argument {
		public String name;
		public ArgumentValue value;

		@Override
		public String toString() {
			return name + " = " + value;
		}
	}

	public static enum ArgumentType {
		DOUBLE, FLOAT, LONG, INTEGER, STRING, BOOLEAN, REFERENCE, BOUND
	}

	public static interface ArgumentValue {
	}

	public static class ArrayArgumentValue implements ArgumentValue {
		public List<SingleArgumentValue> values;

		@Override
		public String toString() {
			return values.toString();
		}
	}

	public static class SingleArgumentValue implements ArgumentValue {
		public ArgumentType type;
		public String value;
		public ArgumentValue defaultValue;

		@Override
		public String toString() {
			return value + "(" + type + ")" + "(" + defaultValue + ")";
		}
	}
	
	static class MetaAnnotationParser {
		static enum ReaderFunction {
			STRING {
				public boolean continueReading(int c, boolean isEscaped) {
					return '"' != c || isEscaped;
				}
			},
			DIGIT {
				public boolean continueReading(int c, boolean isEscaped) {
					return (c >= '0' && c <= '9') || c == '.' || c == '-';
				}
			},
			REFERENCE {
				public boolean continueReading(int c, boolean isEscaped) {
					return Character.isJavaIdentifierPart(c) || c == '.';
				}
			},
			NAME {
				public boolean continueReading(int c, boolean isEscaped) {
					return Character.isJavaIdentifierPart(c);
				}
			};
			
			abstract boolean continueReading(int c, boolean isEscaped);
		}
		
		private static class ParseState {
			public ParseState(String input) {
				this.input = input;
			}
			
			int pos = 0;
			String input;
			
			boolean read(char c) {
				boolean a = canRead(c);
				if (!a) invalidate();
				pos++;
				return a;
			}
			
			boolean tryRead(char c) {
				boolean a = canRead(c);
				if (a) pos++;
				return a;
			}
			
			boolean tryRead(String s) {
				int stored = pos;
				String name = readName();
				if (s.equals(name)) {
					return true;
				} else {
					pos = stored;
				}
				return false;
			}
			
			boolean canRead(char c) {
				skipWhitespace();
				if (end()) return false;
				return input.charAt(pos) == c;
			}
			
			boolean end() {
				return pos >= input.length();
			}
			
			String read(ReaderFunction readerFunction) {
				skipWhitespace();
				int start = pos;
				
				boolean isEscaped = false;
				int currentCodePoint;
				while (!end() && readerFunction.continueReading(currentCodePoint = input.codePointAt(pos), isEscaped)) {
					isEscaped = '\\' == currentCodePoint && !isEscaped;
					pos++;
				}
				
				String substring = input.substring(start, pos);
				if (substring.length() == 0) {
					substring = null;
				}
				return substring;
			}
			
			String readDigit() {
				return read(ReaderFunction.DIGIT);
			}
			
			String readString() {
				return read(ReaderFunction.STRING);
			}
			
			String readReference() {
				return read(ReaderFunction.REFERENCE);
			}
			
			String readName() {
				return read(ReaderFunction.NAME);
			}
			
			String readBoolean() {
				if (tryRead("true")) return "true";
				if (tryRead("false")) return "false";
				return null;
			}

			void skipWhitespace() {
				while (!end() && Character.isWhitespace(input.codePointAt(pos))) {
					pos++;
				}
			}
			
			void invalidate() {
				throw new IllegalArgumentException("Invalid input at character " + pos + "\n" + this);
			}
			
			@Override
			public String toString() {
				return input + "\n" + new String(new char[pos]).replace("\0", " ") + "^";
			}
		}
		
		static List<Annotation> parse(String input) {
			List<Annotation> annotations = new ArrayList<Annotation>();
			
			ParseState state = new ParseState(input);
			Annotation annotation;
			while ((annotation = parseAnnotation(state)) != null) {
				annotations.add(annotation);
			}
			
			if (!state.end()) {
				state.invalidate();
			}
			
			return annotations;
		}
		
		private static Annotation parseAnnotation(ParseState state) {
			Annotation annotation = new Annotation();
			
			if (state.tryRead('@')) {
				String name = state.readReference();
				if (name == null) state.invalidate();
				
				annotation.name = name;
				annotation.args = new ArrayList<Argument>();
				
				if (state.tryRead('(')) {
					annotation.args = parseParameters(state);
					state.read(')');
				}
				annotation.replace = state.tryRead('!');
				annotation.add = state.tryRead('+');
				return annotation;
			}
			return null;
		}
		
		private static List<Argument> parseParameters(ParseState state) {
			List<Argument> args = new ArrayList<Argument>();
			
			Argument argument;
			do {
				argument = parseParameter(state);
			} while (argument != null && args.add(argument) && state.tryRead(','));
			
			if (args.size() == 0) {
				ArgumentValue value = parseArgumentValue(state);
				if (value != null) {
					Argument implicitValueArgument = new Argument();
					implicitValueArgument.name = "value";
					implicitValueArgument.value = value;
					args.add(implicitValueArgument);
				}
			}
			return args;
		}
		
		
		private static ArgumentValue parseArgumentValue(ParseState state) {
			ArrayArgumentValue arrayArgumentValue = parseArrayArgumentValue(state);
			if (arrayArgumentValue != null) return arrayArgumentValue;
			
			ArgumentValue parseValue = parseSingleArgumentValue(state);
			if (parseValue != null) return parseValue;
			
			return null;
		}
	
		private static SingleArgumentValue parseSingleArgumentValue(ParseState state) {
			SingleArgumentValue singleArgumentValue = new SingleArgumentValue();
			
			if (state.tryRead('"')) {
				singleArgumentValue.value = state.readString();
				singleArgumentValue.type = ArgumentType.STRING;
				state.read('"');
				return singleArgumentValue;
			}
			boolean negativ = state.tryRead('-');
			String digit = state.readDigit();
			if (digit != null) {
				boolean isDouble = state.tryRead('d') || state.tryRead('D');
				boolean isFloat = state.tryRead('f') || state.tryRead('F');
				boolean isLong = state.tryRead('l') || state.tryRead('L');
				boolean isFloatingPoint = digit.contains(".");
				
				if (isDouble) {
					singleArgumentValue.type = ArgumentType.DOUBLE;
				} else if (isFloat) {
					singleArgumentValue.type = ArgumentType.FLOAT;
				} else if (isLong) {
					singleArgumentValue.type = ArgumentType.LONG;
				} else if (isFloatingPoint) {
					singleArgumentValue.type = ArgumentType.DOUBLE;
				} else {
					singleArgumentValue.type = ArgumentType.INTEGER;
				}
				
				singleArgumentValue.value = negativ ? "-" + digit : digit;
				return singleArgumentValue;
			}
			if (state.tryRead('<')) {
				singleArgumentValue.value = state.readName();
				singleArgumentValue.type = ArgumentType.BOUND;
				if (state.tryRead('|')) {
					singleArgumentValue.defaultValue = parseArgumentValue(state);
				}
				state.read('>');
				return singleArgumentValue;
			}
			String bool = state.readBoolean();
			if (bool != null) {
				singleArgumentValue.value = bool;
				singleArgumentValue.type = ArgumentType.BOOLEAN;
				return singleArgumentValue;
			}
			String reference = state.readReference();
			if (reference != null) {
				singleArgumentValue.value = reference;
				singleArgumentValue.type = ArgumentType.REFERENCE;
				return singleArgumentValue;
			}
			return null;
		}
	
		private static ArrayArgumentValue parseArrayArgumentValue(ParseState state) {
			if (state.tryRead('{')) {
				ArrayArgumentValue arrayArgumentValue = new ArrayArgumentValue();
				arrayArgumentValue.values = new ArrayList<SingleArgumentValue>();
				
				SingleArgumentValue v;
				do {
					v = parseSingleArgumentValue(state);
				} while (v != null && arrayArgumentValue.values.add(v) && state.tryRead(','));
				
				state.read('}');
				return arrayArgumentValue;
			}
			return null;
		}
	
		private static Argument parseParameter(ParseState state) {
			int pos = state.pos;
			
			String name = state.readName();
			if (name != null && state.tryRead('=')) {
				ArgumentValue value = parseArgumentValue(state);
				if (value != null) {
					Argument arg = new Argument();
					arg.name = name;
					arg.value = value;
					return arg;
				}
			}
			
			state.pos = pos;
			return null;
		}
	}
}
