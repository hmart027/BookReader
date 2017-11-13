package edu.fiu.cate.breader;

import image.tools.ImagePanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import java.awt.Font;
import java.awt.BorderLayout;

public class CaptureSettings extends JFrame {

	private static final long serialVersionUID = 8677323488418863084L;
//	private JPanel contentPane;
	private ImagePanel captureDisplay;
	private JButton btnCapture;
	
	//Interpolation Methods
	private int interpolationMethod = 3;
	private JRadioButton rdbtnLinear;
	private JRadioButton rdbtnCubic;
	private JRadioButton rdbtnLazano;
	
	//Correction
	private int correctionMethod = 3;
	private JRadioButton rdbtnNone;
	private JRadioButton rdbtnFlattening;
	private JRadioButton rdbtnFlatExt;
	private JPanel panel_2;
	private JPanel panel_3;
	private JPanel panel_4;
	
	/**
	 * Create the frame.
	 */
	public CaptureSettings() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 655, 571);
		NumberFormat nformat = NumberFormat.getNumberInstance();
		nformat.setMinimumFractionDigits(1);
		
		panel_2 = new JPanel();
		setContentPane(panel_2);
			
		captureDisplay = new ImagePanel(new Dimension(50, 50));
		captureDisplay.setBackground(Color.DARK_GRAY);
		
		panel_3 = new JPanel();
		
		panel_4 = new JPanel();
		
		JLabel lblInterpolation = new JLabel("Interpolation");
		lblInterpolation.setFont(lblInterpolation.getFont().deriveFont(lblInterpolation.getFont().getStyle() | Font.BOLD));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		rdbtnLinear = new JRadioButton("Linear");
		rdbtnCubic = new JRadioButton("Cubic");
		rdbtnLazano = new JRadioButton("LANCZOS");
		rdbtnLazano.setSelected(true);
		
		rdbtnLinear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JRadioButton)e.getSource()).isSelected()){
					interpolationMethod = 1;
					rdbtnCubic.setSelected(false);
					rdbtnLazano.setSelected(false);
				}	
			}
		});
		rdbtnCubic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JRadioButton)e.getSource()).isSelected()){
					interpolationMethod = 2;
					rdbtnLinear.setSelected(false);
					rdbtnLazano.setSelected(false);
				}	
			}
		});
		rdbtnLazano.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JRadioButton)e.getSource()).isSelected()){
					interpolationMethod = 3;
					rdbtnLinear.setSelected(false);
					rdbtnCubic.setSelected(false);
				}	
			}
		});
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(rdbtnLinear)
						.addComponent(rdbtnCubic)
						.addComponent(rdbtnLazano))
					.addContainerGap(8, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnLinear)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnCubic)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnLazano)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
		JLabel lblCorrection = new JLabel("Correction");
		lblCorrection.setFont(lblCorrection.getFont().deriveFont(lblCorrection.getFont().getStyle() | Font.BOLD));
		
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		rdbtnNone = new JRadioButton("None");
		rdbtnFlattening = new JRadioButton("Flattening");
		rdbtnFlatExt = new JRadioButton("Flat + Ext");
		rdbtnFlatExt.setSelected(true);
		
		rdbtnNone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JRadioButton)e.getSource()).isSelected()){
					correctionMethod = 1;
					rdbtnFlattening.setSelected(false);
					rdbtnFlatExt.setSelected(false);
				}	
			}
		});
		rdbtnFlattening.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JRadioButton)e.getSource()).isSelected()){
					correctionMethod = 2;
					rdbtnNone.setSelected(false);
					rdbtnFlatExt.setSelected(false);
				}	
			}
		});
		rdbtnFlatExt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JRadioButton)e.getSource()).isSelected()){
					correctionMethod = 3;
					rdbtnNone.setSelected(false);
					rdbtnFlattening.setSelected(false);
				}	
			}
		});
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnNone)
					.addContainerGap(4, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(rdbtnFlatExt)
					.addContainerGap())
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnFlattening)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnNone)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(rdbtnFlattening)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnFlatExt)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		btnCapture = new JButton("Capture\r\nNow");
		btnCapture.setEnabled(false);
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 252, Short.MAX_VALUE)
					.addComponent(btnCapture, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
					.addGap(18))
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblInterpolation)
					.addGap(24)
					.addComponent(lblCorrection)
					.addContainerGap(402, Short.MAX_VALUE))
		);
		gl_panel_4.setVerticalGroup(
			gl_panel_4.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCorrection)
						.addComponent(lblInterpolation))
					.addGap(7)
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(12, Short.MAX_VALUE))
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap(39, Short.MAX_VALUE)
					.addComponent(btnCapture, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
					.addGap(31))
		);
		panel_4.setLayout(gl_panel_4);
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel_3, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
						.addComponent(captureDisplay, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
					.addGap(1))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addComponent(captureDisplay, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
					.addGap(6)
					.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
					.addGap(10))
		);
		panel_3.setLayout(new BorderLayout(0, 0));
		panel_3.add(panel_4);
		panel_2.setLayout(gl_panel_2);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		pack();
	}
		
	public int getInterpolationMethod(){
		return interpolationMethod;
	}
	
	public int getCorrectionMethod(){
		return correctionMethod;
	}
		
	public void setImage(BufferedImage img){
		this.captureDisplay.setImage(img);
	}
		
	public void addCaptureActionListener(ActionListener a){
		btnCapture.addActionListener(a);
	}
	
	public void enableCapture(boolean e){
		btnCapture.setEnabled(e);
	}
	
	public static void main(String[] args){
		new CaptureSettings();
	}
}
