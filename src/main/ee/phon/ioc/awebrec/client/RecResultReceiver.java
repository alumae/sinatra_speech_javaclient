package ee.phon.ioc.awebrec.client;

public interface RecResultReceiver {

	void receiveResult(String result, boolean isFinal);
}
