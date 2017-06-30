package edu.fiu.cate.breader;

import image.tools.ImagePanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JSlider;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

public class CaptureSettings extends JFrame {

	private static final long serialVersionUID = 8677323488418863084L;
	private JPanel contentPane;
	private ImagePanel captureDisplay;
	private JSlider slider;
	private JFormattedTextField textField;
	private JCheckBox checkBox;
	private JFormattedTextField minValueField;
	private JFormattedTextField maxValueField;
	private JFormattedTextField actualMax;
	
	float normVal=6000;
	float maxNorm = 65535;
	float minNorm = 0;
	

	/**
	 * Create the frame.
	 */
	public CaptureSettings() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 544, 424);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);	

		JLabel lblNormalization = new JLabel("Normalization");
		captureDisplay = new ImagePanel(new Dimension(50, 50));
		captureDisplay.setBackground(Color.DARK_GRAY);	
		slider = new JSlider();
		slider.setMaximum(1000);
		slider.setValue((int)(normVal/maxNorm*1000));
		NumberFormat nformat = NumberFormat.getNumberInstance();
		nformat.setMinimumFractionDigits(1);
		textField = new JFormattedTextField(nformat);
		textField.setColumns(10);
		checkBox = new JCheckBox("");
		
		textField.setValue(normVal);
		slider.addChangeListener(new ChangeListener() {		
			@Override
			public void stateChanged(ChangeEvent e) {
				float v = ((JSlider)e.getSource()).getValue();
				normVal = (maxNorm-minNorm)*v/1000.0f;
				if(textField!=null){
					textField.setText(normVal+"");
				}
			}
		});

		textField.addPropertyChangeListener("value", new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				float v = ((Number)textField.getValue()).floatValue();
				if(v<minNorm) v=minNorm;
				if(v>maxNorm) v=maxNorm;
				normVal = v;
				int s = (int) ((normVal-minNorm)/(maxNorm-minNorm)*1000.0f);
				slider.setValue(s);
			}
		});
		
		minValueField = new JFormattedTextField(nformat);
		minValueField.setColumns(10);
		minValueField.setValue(minNorm);
		minValueField.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				float v = ((Number)minValueField.getValue()).floatValue();
				if(v>maxNorm) v=maxNorm;
				int s = slider.getValue();
				minNorm = v;
				normVal = (maxNorm-minNorm)*s/1000.0f-minNorm;
				if(textField!=null){
					textField.setText(normVal+"");
				}
				System.out.println("Min: "+minNorm);
			}
		});
		
		maxValueField = new JFormattedTextField(nformat);
		maxValueField.setColumns(10);
		maxValueField.setValue(maxNorm);
		maxValueField.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				float v = ((Number)maxValueField.getValue()).floatValue();
				if(v<minNorm) v=minNorm;
				int s = slider.getValue();
				maxNorm = v;
				normVal = (maxNorm-minNorm)*s/1000.0f-minNorm;
				if(textField!=null){
					textField.setText(normVal+"");
				}
			}
		});
		
		JLabel lblMax = new JLabel("Max:");
		JLabel lblMin = new JLabel("Min:");
		
		actualMax = new JFormattedTextField(nformat);
		actualMax.setEditable(false);
		actualMax.setColumns(10);
			
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(18, 18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(captureDisplay, GroupLayout.PREFERRED_SIZE, 739, 2000)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(checkBox)
								.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblNormalization))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(42)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_contentPane.createSequentialGroup()
											.addComponent(lblMin)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(minValueField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_contentPane.createSequentialGroup()
											.addComponent(lblMax)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(maxValueField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGap(129)
											.addComponent(actualMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))))))
					.addContainerGap(24, 24))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(24)
					.addComponent(captureDisplay, GroupLayout.PREFERRED_SIZE, 238, 1000)
					.addGap(39)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(checkBox)
						.addComponent(lblNormalization))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblMin)
								.addComponent(minValueField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblMax)
								.addComponent(maxValueField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(actualMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addGap(32))
		);
		contentPane.setLayout(gl_contentPane);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		pack();
	}
		
	public float getNormVal(){
		return normVal;
	}
		
	public void setImage(BufferedImage img){
		this.captureDisplay.setImage(img);
	}
	
	public void setActualMax(int max){
		this.actualMax.setText(max+"");
	}
}
