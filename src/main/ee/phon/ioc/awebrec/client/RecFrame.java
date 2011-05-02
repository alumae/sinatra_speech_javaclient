package ee.phon.ioc.awebrec.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JButton;

import edu.cmu.sphinx.frontend.BaseDataProcessor;
import edu.cmu.sphinx.frontend.Data;
import edu.cmu.sphinx.frontend.DataProcessingException;
import edu.cmu.sphinx.frontend.DataStartSignal;
import edu.cmu.sphinx.frontend.DoubleData;
import edu.cmu.sphinx.frontend.util.FrontEndUtils;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.frontend.util.VUMeter;
import edu.cmu.sphinx.frontend.util.VUMeterPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Properties;

public class RecFrame extends JInternalFrame implements RecResultReceiver {

	class MyVUMeterMonitor extends BaseDataProcessor {
		   @Override
		    public Data getData() throws DataProcessingException {
		        Data d = getPredecessor().getData();

		        // show the panel only if  a microphone is used as data source
		        if (d instanceof DataStartSignal)
		            vuMeterPanel.setVisible(FrontEndUtils.getFrontEndProcessor(this, Microphone.class) != null);

		        if (d instanceof DoubleData)
		            vumeter.calculateVULevels(d);

		        return d;
		    }
		
	}
	
	private Properties configuration = new Properties();
	private JPanel contentPane;
	final private VUMeter vumeter;
	final VUMeterPanel vuMeterPanel;
	final MyVUMeterMonitor monitor;
	private boolean speaking;
	private boolean stopping;
	private RecSessionHandler recSessionHandler;
	private JTextArea textArea;
	
	public MyVUMeterMonitor getMonitor() {
		return monitor;
	}


	/**
	 * Create the frame.
	 */
	public RecFrame() {
		this(new Properties());
	}


	/**
	 * Create the frame.
	 * @param confuguration TODO
	 */
	public RecFrame(Properties configuration) {
		this.configuration = configuration;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		contentPane.add(textArea, BorderLayout.CENTER);
		
		final JButton btnStart = new JButton("Speak");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!speaking) {
					try {
						stopping = false;
						btnStart.setText("Stop");
						
						startRec();
						speaking = true;
					} catch (Exception e) {
						JOptionPane.showMessageDialog(RecFrame.this, "Couldn't create recognizer session: " + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);

						btnStart.setText("Speak");
						btnStart.setEnabled(true);
						speaking = false;
					}
					
				} else {
					stopping = true;
					btnStart.setText("Stopping");
					btnStart.setEnabled(false);
					stopRec();
					btnStart.setText("Speak");
					btnStart.setEnabled(true);
					speaking = false;
				}
			}

		});
		contentPane.add(btnStart, BorderLayout.SOUTH);
		
		vumeter = new VUMeter();

        vuMeterPanel = new VUMeterPanel();
        vuMeterPanel.setVu(vumeter);
        vuMeterPanel.start();
        contentPane.add(vuMeterPanel, BorderLayout.EAST);
        
        monitor = new MyVUMeterMonitor();
	}


	private void stopRec() {
		
		try {
			recSessionHandler.waitFinal();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		recSessionHandler = null;
		
		
	}

	private void startRec() throws IOException, NotAvailableException {
		recSessionHandler = new RecSessionHandler(this, configuration);				
	}

	
	public void startMicrophone() {
		Microphone mic = new Microphone( 16000, 16, 1,
                true, true, true, 10, false,
                "selectChannel", 2, "default", 6400);

      mic.initialize();
      mic.startRecording();

      
      getMonitor().setPredecessor(mic);
      Thread eq = new Thread() {

    	  
          @Override
		public void run() {
				try {
			        while (true) {
			        	Data d = getMonitor().getData();
			        	if ((recSessionHandler != null) && (speaking)) {
					        if (d instanceof DoubleData) {
					        	double [] doubleBuffer = ((DoubleData)d).getValues();
					        	byte [] byteBuffer = new byte[doubleBuffer.length * 2];
					        	for (int i = 0; i< doubleBuffer.length; i++) {
					        		short s = (short) doubleBuffer[i];
					        		// FIXME: not sure it always works (endianness)
					        		byteBuffer[i * 2]     = (byte)(s & 0x00FF);
					        		byteBuffer[i * 2 + 1] = (byte)((s & 0xFF00)>>8);
					        	}
					        	if (stopping) {
					        		recSessionHandler.setFinishing(true);
					        	}

					        	recSessionHandler.dataFromMic(byteBuffer);
					        	if (stopping) {
					        		speaking = false;
					        	}
					        }
			        	}
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		eq.setDaemon(true);
		eq.start();
		
	}
      

      
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final RecFrame frame = new RecFrame(new Properties());
					frame.setVisible(true);
					frame.startMicrophone();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}


	@Override
	public void receiveResult(String result, boolean is) {
		textArea.setText(result);
		
	}
	
}
