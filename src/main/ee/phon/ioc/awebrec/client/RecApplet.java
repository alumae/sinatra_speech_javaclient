package ee.phon.ioc.awebrec.client;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import java.util.Properties;

public class RecApplet extends JApplet {

	public static final String BASE_URL = "base_url";
	
	private RecPanel recUi;

	private Properties conf;

	/**
	 * Create the applet.
	 */
	public RecApplet() {

		conf = new Properties();
		recUi = new RecPanel(conf);
		getContentPane().add(recUi, BorderLayout.CENTER);
		recUi.setVisible(true);

	}
	
	@Override
	public void init() {
		String baseUrl   = getParameter(BASE_URL);
		if (baseUrl != null) {
			conf.setProperty(AWebRecSession.CONF_BASE_URL, baseUrl);
		}
	}
	
	@Override
	public void start() {
		recUi.startMicrophone();
	}
	

	@Override
	public void stop() {
		recUi.stopMicrophone();
	}

}
