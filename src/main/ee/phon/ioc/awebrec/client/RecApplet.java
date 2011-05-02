package ee.phon.ioc.awebrec.client;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import java.util.Properties;

public class RecApplet extends JApplet {

	public static final String BASE_URL = "base_url";
	
	private RecPanel fr;

	private Properties conf;

	/**
	 * Create the applet.
	 */
	public RecApplet() {

		conf = new Properties();
		fr = new RecPanel(conf);
		getContentPane().add(fr, BorderLayout.CENTER);
		fr.setVisible(true);

	}
	
	@Override
	public void init() {
		
		String baseUrl   = getParameter(BASE_URL);
		if (baseUrl != null) {
			conf.setProperty(AWebRecSession.CONF_BASE_URL, baseUrl);
		}
		
		fr.startMicrophone();

	}

}
