package ee.phon.ioc.awebrec.client;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import junit.framework.TestCase;

public class XMLUtilsTest extends TestCase {

	public void testLoad() throws IOException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		 + "<recognizer_session>\n" 
		 + "  <closed_at></closed_at>\n"
		 + "  <result>foo</result>\n"
		 + "</recognizer_session>";
		
		Map<String, String> props = XMLUtils.load(new StringReader(xml));
		System.out.println(props.toString());
		assertEquals("foo", props.get("recognizer_session.result"));
		assertEquals(null, props.get("recognizer_session.closed_at"));
	}
}
