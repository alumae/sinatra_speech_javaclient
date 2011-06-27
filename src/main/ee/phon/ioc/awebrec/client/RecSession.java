package ee.phon.ioc.awebrec.client;

import java.io.IOException;

public interface RecSession {

	void create() throws IOException, NotAvailableException;
	
	void sendChunk(byte[] bytes, boolean isLast) throws IOException;

	String getCurrentResult() throws IOException;
	
	boolean isFinished(); 
	
	void cancel();
}
