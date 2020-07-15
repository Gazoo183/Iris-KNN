import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Main {

	public static void main(String[] args) {
		
		JTextField kValue;
		JTextField[] textFields;
		JTextField result;
		JDialog dialog;
		
		JFrame frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(0, 0, 50, 0));
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EmptyBorder(0, 10, 0, 10));
		frame.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(0, 5, 0, 0));
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setBorder(new EmptyBorder(75, 0, 55, 5));
		panel_1.add(horizontalBox);
		
		JLabel lblK = new JLabel("k: ");
		lblK.setFont(new Font("Tahoma", Font.PLAIN, 18));
		horizontalBox.add(lblK);
		
		kValue = new JTextField();
		horizontalBox.add(kValue);
		kValue.setColumns(10);
		
		JLabel[] labels=new JLabel[countColumns("iristrain.csv")];
		textFields=new JTextField[countColumns("iristrain.csv")];
		Box[] boxes=new Box[countColumns("iristrain.csv")];
		
		for(int i=0;i<countColumns("iristrain.csv");i++) {
			
			boxes[i] = Box.createHorizontalBox();
			boxes[i].setBorder(new EmptyBorder(75,0,55,5));
			panel_1.add(boxes[i]);
			
			labels[i] = new JLabel("x"+(i+1)+": ");
			labels[i].setFont(new Font("Tahoma", Font.PLAIN, 18));
			boxes[i].add(labels[i]);
			
			textFields[i] = new JTextField();
			textFields[i].setColumns(2);
			boxes[i].add(textFields[i]);
		
		}
		
		dialog=new JDialog();
		dialog.setTitle("Result");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.setBounds(100, 100, 300, 200);
		dialog.getContentPane().setLayout(null);
		
		result = new JTextField();
		result.setHorizontalAlignment(JTextField.CENTER);
		result.setEditable(false);
		result.setBounds(72, 56, 146, 26);
		dialog.getContentPane().add(result);
		result.setColumns(10);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				result.setText("");
				kValue.setText("");
				for(JTextField textField : textFields)
					textField.setText("");
				dialog.setVisible(false);
			}
		});
		btnOk.setBackground(SystemColor.controlHighlight);
		btnOk.setBounds(89, 115, 115, 29);
		dialog.getContentPane().add(btnOk);
		
		JButton btnCalculate = new JButton("Calculate");
		btnCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				Vector[] trainVectors=new Vector[countLines("iristrain.csv")];
				fill(trainVectors, "iristrain.csv");
				Vector[] closestV=new Vector[Integer.parseInt(kValue.getText())];
				boolean empty=false;
				for(int i=0;i<textFields.length;i++) {
					if(textFields[i].getText().equals("")==true) {
						empty=true;
						break;
					}
				}
				
				if(empty==false) {
					float[] atrybuty=new float[textFields.length];
					for(int i=0;i<atrybuty.length;i++) {
						atrybuty[i]=Float.parseFloat(textFields[i].getText());
					}
					Vector vector=new Vector(atrybuty);  
					result.setText(kNN(closestV, trainVectors, vector));
					dialog.setVisible(true);
				} else {
					Vector[] testVectors=new Vector[countLines("iristest.csv")];
					fill(testVectors, "iristest.csv");
					int correct=0;
					for(int i=0;i<testVectors.length;i++) {
						if(testVectors[i].species.equals(kNN(closestV,trainVectors,testVectors[i]))) {
							correct++;
						}
					}
					int tmp=correct*100/testVectors.length;
					result.setText(correct+" "+tmp+"%");
					dialog.setVisible(true);
				}
				
			}
		});
		btnCalculate.setBackground(SystemColor.controlHighlight);
		panel.add(btnCalculate);
		
		frame.setVisible(true);
		
	}

	public static int countLines(String file) {
		int toReturn=0;
		Scanner sc;
		try {
			sc=new Scanner(new File(file));
			sc.nextLine();
			while(sc.hasNextLine()) {
				toReturn++;
				sc.nextLine();
			}
		} catch (FileNotFoundException e) {}
		return toReturn;
	}
	
	public static int countColumns(String file) {
		int toReturn=0;
		Pattern p=Pattern.compile(",");
		Matcher m;
		Scanner sc;
		try {
			sc=new Scanner(new File(file));
			sc.nextLine();
			m=p.matcher(sc.nextLine());
			while(m.find())
				toReturn++;
		} catch (FileNotFoundException e) {}
		return toReturn-1;
		
	}
	
	public static void fill(Vector[] vectors, String file) {
		Scanner sc;
		try {
			sc=new Scanner(new File(file));
			sc.nextLine();
			for(int i=0;sc.hasNextLine();i++) {
				String[] tmp=sc.nextLine().split(",");
				float[] tmp1=new float[countColumns(file)];
				for(int j=0;j<tmp1.length;j++) {
					tmp1[j]=Float.parseFloat(tmp[j+1]);
				}
				String tmp2=tmp[tmp.length-1];
				vectors[i]=new Vector(tmp1, tmp2);
			} sc.close();
		} catch (NumberFormatException e) {} catch (FileNotFoundException e) {}
	}
	
	public static String kNN(Vector[] closestV, Vector[] vectors, Vector z) {
		for(int i=0;i<vectors.length;i++) {
			float dist=0;
			for(int j=0;j<z.measures.length;j++) {
				dist+=Math.abs(vectors[i].measures[j]-z.measures[j]);
			}
			vectors[i].dist=dist;
		} quickSort(vectors, 0, vectors.length-1);
		for(int i=0;i<closestV.length;i++) {
			closestV[i]=vectors[i];
		}
		
		String toReturn="";
		int n=0;
		for(int i=0;i<closestV.length;i++) {
			String tmp=closestV[i].species;
			int tmp1=0;
			for(int j=0;j<closestV.length;j++) {
				if(closestV[j].species.equals(tmp))
					tmp1++;
			}
			if(tmp1>n) {
				toReturn=tmp;
				n=tmp1;
			}
		}
		return toReturn;
	}
	
	public static void quickSort(Vector[] vectors, int start, int end) {
		if(start<end) {
			int partitionIndex=partition(vectors, start, end);
			quickSort(vectors, start, partitionIndex-1);
			quickSort(vectors, partitionIndex+1, end);
		}
	}
	
	public static int partition(Vector[] vectors, int start, int end) {
		float pivot=vectors[end].dist;
		int i=start-1;
		for(int j=start;j<end;j++) {
			if(vectors[j].dist<=pivot) {
				i++;
				Vector tmp=vectors[i];
				vectors[i]=vectors[j];
				vectors[j]=tmp;
			}
		}
		Vector tmp=vectors[i+1];
		vectors[i+1]=vectors[end];
		vectors[end]=tmp;
		return i+1;	
	}
}
