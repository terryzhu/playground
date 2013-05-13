
/**
 * This is used to batch evernote diary with simple interface
 * 
 * @author terry zhu
 * 
 */
public class EverNotePorting {
  public static final String DELIMETER = "::::";
	public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE en-export SYSTEM \"http://xml.evernote.com/pub/evernote-export.dtd\"><en-export export-date=\"20130507T023920Z\" application=\"Evernote/Windows\" version=\"4.x\">";
	public static final String HEADER_END = "</en-export>";
	public static final String CDATA = "<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\"><en-note style=\"word-wrap: break-word; -webkit-nbsp-mode: space; -webkit-line-break: after-white-space;\">";
	public static final String CDATA_END = "</en-note>]]>";
	List<String> noteList = new ArrayList<String>();

	public String newENEX() {
		return HEADER + newNoteList() + HEADER_END;
	}

	/**
	 * create a string like
	 * <p>
	 * &ltnote>XXX&lt/note>
	 * <p>
	 * &ltnote>XXX&lt/note>
	 * 
	 * @return
	 */
	private String newNoteList() {
		StringBuilder sb = new StringBuilder();
		for (String note : noteList) {
			sb.append(note);
		}
		return sb.toString();
	}

	public void newNote(String content, String create) {
		StringBuilder note = new StringBuilder();
		String time = addTag("created", adjustTimeUTC(create)) + addTag("updated", adjustTimeUTC(create));
		note.append(addTag("note", addTag("title", adjustTime(create)) + newContent(content, adjustTime(create)) + time));
		noteList.add(note.toString());
	}

	/**
	 * create a string like &ltcontent>XXX&lt/content>, which also contains some
	 * META info
	 * 
	 * @param content
	 * @param time
	 * @return
	 */
	private String newContent(String content, String time) {
		String string = addTag("content", CDATA + time + content + CDATA_END);
		return string;
	}

	private static String addTag(String tag, String text) {
		return "<" + tag + ">" + text + "</" + tag + ">";
	}

	/**
	 * 2013/5/6 --> 20130506T133325Z
	 */
	public static String adjustTimeUTC(String time) {
		String[] strings = time.split("/");
		if (Integer.valueOf(strings[1]) < 10) {
			strings[1] = "0" + strings[1];
		}
		if (Integer.valueOf(strings[2]) < 10) {
			strings[2] = "0" + strings[2];
		}
		return strings[0] + strings[1] + strings[2] + "T120000Z";
	}

	/**
	 * 2013/5/6 -->2013.5.6
	 */
	public static String adjustTime(String time) {
		return time.replace("/", ".");
	}

	public static List<String> parseFile() throws IOException {
		List<String> list = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				EverNotePorting.class.getResourceAsStream("diary")));
		String line = null;
		String time = "";
		StringBuilder content = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			if (line.contains("2010/")) { // only contains 2010 dairy
				if (time.length() != 0) {
					list.add(time + DELIMETER + content.toString());
				}
				time = line;
				content = new StringBuilder();
			} else {
				content.append("<div>" + line + "</div>");
			}
		}
		list.add(time + DELIMETER + content.toString());
		return list;
	}

	public static String getFileContent(File file) throws Exception {
		InputStream iStream = new FileInputStream(file);

		long l = file.length();
		byte[] b = new byte[(int) l];
		iStream.read(b);
		RTFEditorKit rtfParser = new RTFEditorKit();
		Document document = rtfParser.createDefaultDocument();
		rtfParser.read(new ByteArrayInputStream(b), document, 0);
		String text = document.getText(0, document.getLength());
		StringBuilder sb = new StringBuilder();
		String[] lines = text.split("\n");
		for (String string : lines) {
			sb.append("<div>").append(string).append("</div>");
		}
		iStream.close();
		return sb.toString();
	}

	// convert Diary110202 to 2011/02/02
	public static String adjustFileNameToTime(String file) {
		int year = Integer.valueOf(file.substring(5, 7));
		int month = Integer.valueOf(file.substring(7, 9));
		int day = Integer.valueOf(file.substring(9, 11));
		return "20" + year + "/" + month + "/" + day;
	}

	/**
	 * Usage:
	 * <p>
	 * EverNotePorting evernote = new EverNotePorting();
	 * <p>
	 * evernote.newNote(content, createTime);
	 * <p>
	 * System.out.println(evernote.newENEX());
	 * <p>
	 * The output is a xml format, we could copy it to a txt file and rename it
	 * with *.enex and imported by evernote
	 */
	public static void main(String[] args) throws Exception {
		/**
		 * this part is used to generate evenote database from a file which
		 * format is like:
		 * <p>
		 * 2012/12/12
		 * <p>
		 * this is a note
		 * <p>
		 * this is another line
		 * <p>
		 * 2012/12/13 this is another line
		 * <p>
		 */

		// List<String> list = parseFile();
		// EverNotePorting evernote = new EverNotePorting();
		// for (String string : list) {
		// String[] strings = string.split(DELIMETER);
		// evernote.newNote(strings[1], strings[0]);
		// }
		// System.out.println(evernote.newENEX());

		/**
		 * this part is used to generate evenote database from many files, whose
		 * name is Diary110202 and content is diary content:
		 */

		// EverNotePorting evernote = new EverNotePorting();
		// File file = new File("src/main/java/com/test");
		// File[] files = file.listFiles();
		// for (File string : files) {
		// if (string.getName().contains("Diary1")) {
		// evernote.newNote(getFileContent(string),
		// adjustFileNameToTime(string.getName()));
		// }
		// }
		// System.out.println(evernote.newENEX());
	}
}
