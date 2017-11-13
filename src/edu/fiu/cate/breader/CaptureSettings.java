package edu.fiu.cate.breader;

import image.tools.ImagePanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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

public class CaptureSettings extends JFrame {

	private static final long serialVersionUID = 8677323488418863084L;
	private JPanel contentPane;
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
	
	/**
	 * Create the frame.
	 */
	public CaptureSettings() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 456);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		captureDisplay = new ImagePanel(new Dimension(50, 50));
		captureDisplay.setBackground(Color.DARK_GRAY);
		NumberFormat nformat = NumberFormat.getNumberInstance();
		nformat.setMinimumFractionDigits(1);
		
		btnCapture = new JButton("Capture\r\nNow");
		btnCapture.setEnabled(false);
		
		JLabel lblInterpolation = new JLabel("Interpolation");
		lblInterpolation.setFont(lblInterpolation.getFont().deriveFont(lblInterpolation.getFont().getStyle() | Font.BOLD));
		
		JLabel lblCorrection = new JLabel("Correction");
		lblCorrection.setFont(lblCorrection.getFont().deriveFont(lblCorrection.getFont().getStyle() | Font.BOLD));
		
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		rdbtnLinear = new JRadioButton("Linear");
		rdbtnCubic = new JRadioButton("Cubic");
		rdbtnLazano = new JRadioButton("LANCZOS");
		rdbtnLazano.setSelected(true);
		
		rdbtnNone = new JRadioButton("None");
		rdbtnFlattening = new JRadioButton("Flattening");
		rdbtnFlatExt = new JRadioButton("Flat + Ext");
		rdbtnFlatExt.setSelected(true);
		
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
			
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(41)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblInterpolation))
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(btnCapture, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE))
								.addComponent(lblCorrection)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(18)
							.addComponent(captureDisplay, GroupLayout.PREFERRED_SIZE, 357, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(448, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(24)
					.addComponent(captureDisplay, GroupLayout.PREFERRED_SIZE, 238, 1000)
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblInterpolation)
								.addComponent(lblCorrection))
							.addGap(7)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(panel, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(19))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnCapture, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
							.addGap(40))))
		);
		
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
		contentPane.setLayout(gl_contentPane);
		
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
