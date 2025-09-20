import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

public class ttt {
	static int left = 0;
	static boolean ec = true;

	public static boolean is_number(char c){
		if (c >= '0' && c <= '9') return true;
		return false;
	}

	public static int priority(char c){
		if (c == '+' || c == '-') return 1;
		if (c == '*' || c == '÷' || c == '%') return 2;
		return 0;
	}

	public static void flush(JTextField output, char c){
		if (ec) ;
		else {
			output.setText("0");
			ec = true;
		}

		if (output.getText().equals("0")){
			if (c >= '0' && c <= '9'){
				output.setText(String.valueOf(c));
				return;
			}
			if (c == '('){
				output.setText(String.valueOf(c));
				left++;
				return;
			}
		}
		else{
			if (c < '0' || c > '9'){
				char last_c = output.getText().charAt(output.getText().length() - 1);
				if (c == '('){
					if (is_number(last_c)){
						output.setText(output.getText() + "*" + String.valueOf(c));
					}
					else output.setText(output.getText() + String.valueOf(c));
					left++;
					return;
				}
				if (c == ')'){
					if (left > 0){
						if (output.getText().charAt(output.getText().length()-1)== '('){
							return;
						}
						output.setText(output.getText() + String.valueOf(c));
						left--;
					}
					return;
				}

				
				if (last_c < '0' || last_c > '9'){
					// ( 后不能直接加+-*/%
					if (last_c == '('){
						if (priority(c) > 0){
							return;
						}
					}
					// +-*/%不能叠加，发生则替换
					if (!(c == '(' || c == ')' || last_c == '(' || last_c == ')')){
						String context = output.getText().substring(0, output.getText().length() -1);
						output.setText(context + String.valueOf(c));
						return;
					}
				}
			}
			output.setText(output.getText() + String.valueOf(c));
		}
	}

	public static void clear(JTextField output){
		output.setText("0");
		left = 0;
	}

	public static void back_one(JTextField output){
		if (output.getText().length() == 1){
			output.setText("0");
			return;
		}
		char last_c = output.getText().charAt(output.getText().length() - 1);
		String context = output.getText().substring(0, output.getText().length() -1);
		if (last_c == '(') left--;
		else if (last_c == ')') left++;
		output.setText(context);
	}

	public static void calculate(JTextField output, JFrame f){
		ArrayList<String> suffix = new ArrayList<>();
		Stack<Character> stack = new Stack<>();
		String context = output.getText();

		//填报缺少的右括号，我真是个好人
		for (int i = 1; i <= left; i++){
			context = context + ")";
		}

		int n = 0;
		boolean sign = false;
		//转化为后缀式
		for (char c : context.toCharArray()) {
			if (is_number(c)){
				n = n * 10 + c - '0';
				sign = true;
				continue;
			}
			else{
				if (sign){
					suffix.add(String.valueOf(n));
					n = 0;
					sign = false;
				}
				
			}

			if (c == '('){
				stack.push(c);
			}
			else if (c == ')'){
				while (!stack.isEmpty() && stack.peek() != '(') {
					suffix.add(String.valueOf(stack.pop()));
				}
				stack.pop();
			}
			else if (priority(c) > 0){
				while (!stack.isEmpty() && priority(stack.peek()) > priority(c)) {
					suffix.add(String.valueOf(stack.pop()));
				}
				stack.push(c);
			}
		}
		if (sign){
			suffix.add(String.valueOf(n));
		}
		while (!stack.isEmpty()) {
			suffix.add(String.valueOf(stack.pop()));
		}

		Stack<String> num = new Stack<>();
		for (String s : suffix){
			if (s.charAt(0) >= '0' && s.charAt(0) <= '9'){
				num.push(s);
			}
			else{
				int n2 = Integer.parseInt(num.pop());
				int n1 = Integer.parseInt(num.pop());
				int end = 0;
				if (s.equals("+")) end = n1 + n2;
				else if (s.equals("-")) end = n1 - n2;
				else if (s.equals("*")) end = n1 * n2;
				else if (s.equals("÷")) {
					if (n2 == 0){
						JOptionPane.showMessageDialog(f, "除数不能为0!");
						output.setText("0");
						return;
					}
					end = n1 / n2;
				}
				else if (s.equals("%")) {
					if (n2 == 0){
						JOptionPane.showMessageDialog(f, "除数不能为0!");
						output.setText("0");
						return;
					}
					end = n1 % n2;
				}
				
				num.push(String.valueOf(end));
			}
		}
		output.setText(num.pop());

		ec = false;
	}

	public static void main(String[] args){
		String[] key_c = {"(", ")", "C", "+",
						  "7", "8", "9", "-", 
						  "4", "5", "6", "*",
						  "1", "2", "3", "÷",
						  "<", "0", "=", "%"};
		
		JFrame f = new JFrame("我是计算机的窗口名称");
		f.setSize(450, 655);
		f.setLayout(new FlowLayout());
		f.setLocation(450, 50);
		f.getContentPane().setBackground(new Color(238, 244, 249));

		//总-垂直分布框
		Box bv = Box.createVerticalBox();

		//菜单栏
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("编辑");
		JMenuItem copy = new JMenuItem("复制");
		JMenuItem nian = new JMenuItem("粘贴");

		menubar.setBackground(new Color(238, 244, 249));
		menubar.setMinimumSize(new Dimension(90, 40));
		menubar.setLayout(new FlowLayout(FlowLayout.LEFT));
		menubar.add(menu);

		menu.setPreferredSize(new Dimension(80, 30));
		menu.setFont(new Font(null, 0, 20));
		menu.add(copy);
		menu.add(nian);

		//输出框
		JTextField output = new JTextField("0", 12);

		copy.setPreferredSize(new Dimension(75, 30));
		copy.setFont(new Font(null, 0, 20));
		nian.setPreferredSize(new Dimension(75, 30));
		nian.setFont(new Font(null, 0, 20));
		copy.addActionListener(l -> {
			StringSelection stringSelection = new StringSelection(output.getText());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
			JOptionPane.showMessageDialog(f, "复制成功!");
		});
		nian.addActionListener(l -> {
			try{
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				String context = String.valueOf(clipboard.getData(DataFlavor.stringFlavor));
				output.setText("0");
				for(char i : context.toCharArray()){
					if (('0' <= i && i <= '9') || i == '+' || i == '-' || i == '*' || i == '%' || i == '÷' || i == '/'){
						if (i == '/') i = '÷';
						flush(output, i);
					}
					else throw(new Throwable());
				}
			}
			catch(Throwable t){
				JOptionPane.showMessageDialog(f, "粘贴失败!");
			}
		});

		bv.add(menubar);
		
		//输出框内容实现
		output.setFont(new Font(null, 0, 40));
		output.setHorizontalAlignment(JTextField.RIGHT);
		output.setEditable(false);
		output.setPreferredSize(new Dimension(200, 90));
		output.setBackground(new Color(238, 244, 249));
		bv.add(output);
		bv.add(Box.createVerticalStrut(44));
		
		//设置按键
		Container keys = new Container();
		keys.setLayout(new GridLayout(5, 4, 7, 7));
		keys.setPreferredSize(new Dimension(400, 420));
		for (int i = 0; i < 20; i++){
			Button b = new Button(key_c[i]);
			final int index = i;
			b.setFont(new Font(null, 0, 35));
			if ((i % 4) < 3 && (i / 4) > 0){
				b.setBackground(new Color(255, 255, 255));
			}
			else{
				b.setBackground(new Color(246, 250, 252));
			}

			//触发器连接
			if (i != 2 && i != 16 && i != 18){
				b.addActionListener(l -> {
					flush(output, key_c[index].charAt(0));
				});
			}
			if (i == 2){
				b.addActionListener(l -> {clear(output);});
			}
			if (i == 16){
				b.addActionListener(l -> {back_one(output);});
			}
			if (i == 18){
				b.addActionListener(l -> {calculate(output, f);});
			}
			keys.add(b);
		}
		bv.add(keys);

		f.add(bv);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}