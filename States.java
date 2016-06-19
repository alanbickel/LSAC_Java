package ComLog;

public class States {
	enum progState {

		INIT {

			public String tellState() {
				return "INIT";
			}
		},
		NEW_USER {
			@Override
			public String tellState() {
				return "NEW_USER";
			}
		},
		NEW_ENTRY {
			@Override
			public String tellState() {
				return "NEW_ENTRY";
			}
		},
		EDIT_ENTRY {
			@Override
			public String tellState() {
				return "EDIT_ENTRY";
			}
		},
		BROWSE_ENTRY {
			@Override
			public String tellState() {
				return "BROWSE_ENTRY";
			}
		},
		NEW_TOPIC {
			@Override
			public String tellState() {
				return "NEW_TOPIC";
			}
		},
		EDIT_TOPIC {
			@Override
			public String tellState() {
				return "EDIT_TOPIC";
			}
		},
		BROWSE_TOPIC {
			@Override
			public String tellState() {
				return "BROWSE_TOPIC";
			}
		},
		CONFIG {
			@Override
			public String tellState() {
				return "CONFIG";
			}
		},
		SEARCH_RESULT {
			@Override
			public String tellState() {
				return "SEARCH_RESULT";
			}
		},
		YESTERDAY {
			@Override
			public String tellState() {
				return "YESTERDAY";
			}
		};

		public abstract String tellState();
	}
}
