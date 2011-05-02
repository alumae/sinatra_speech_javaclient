package ee.phon.ioc.awebrec.client;

import java.io.IOException;
import java.util.Properties;

public class RecSessionHandler {
	private final static int ALLOCATION_SIZE = 1000000;
	private final static int FETCH_RESULT_DELAY = 500;
	private final static int SEND_BUFFER_SIZE = 1024*16;
	
	private ByteFIFO buffer;
	private AWebRecSession recSession;
	private String result;
	private boolean finishing = false;
	private final RecResultReceiver resultReceiver;
	
	public boolean isFinishing() {
		return finishing;
	}

	public void setFinishing(boolean finishing) {
		this.finishing = finishing;
	}

	public RecSessionHandler(RecResultReceiver resultReceiver, Properties configuration) throws IOException, NotAvailableException {
		this.resultReceiver = resultReceiver;
		buffer = new ByteFIFO(ALLOCATION_SIZE);
		recSession = new AWebRecSession();
		recSession.setConfiguration(configuration);
		recSession.create();
		Thread handler = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (buffer) {
						while (true) {
							if ((buffer.getSize() > SEND_BUFFER_SIZE) || finishing) {
								//System.out.println("remaining before: " + buffer.remaining());
								byte[] tmp = buffer.removeAll();
								
								//System.out.println("remaining after: " + buffer.remaining());
								System.out.println("Sending chunk, size=" + tmp.length);
								recSession.sendChunk(tmp, finishing);
								
								result = recSession.getCurrentResult();
								if (result != null) {
									RecSessionHandler.this.resultReceiver.receiveResult(result, false);
								}
								
								if (finishing) {
									break;
								}
							} else {
								buffer.wait(FETCH_RESULT_DELAY);
							}
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		handler.setDaemon(true);
		handler.start();
	}
	
	public void dataFromMic(byte[] bytes) {
		try {
			synchronized (buffer) {
				buffer.add(bytes);
				buffer.notify();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void waitFinal() throws InterruptedException, IOException {
		synchronized (buffer) {
			while (!buffer.isEmpty()) {
				buffer.wait();
			}
		}
		boolean finished = false;
		do {
			result = recSession.getCurrentResult();
			finished = recSession.isFinished();
			if (result != null) {
				resultReceiver.receiveResult(result, finished);
			}
			Thread.sleep(FETCH_RESULT_DELAY);
		} while (!finished);
		
	}
	
     	
}
