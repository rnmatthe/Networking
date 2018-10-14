//@author Rachel Matthews

import java.util.Scanner;

public class Homework1{
	
	private static Scanner input = new Scanner(System.in);

	private static int method;
	private static int choice;
	private static String message;
	private static String result = "";

	public static void main(String[] args){

		while(true){

			System.out.println("***********************************");
			System.out.println("Which method would you like to use?");
			System.out.println("1) Checksum \n2) CRC \n3) Hamming Distance \n4) Exit");

			method = input.nextInt();

			if(method == 4){
				break;
			}

			System.out.println("\nWould you like to simulate the sender or receiver?");
			System.out.println("1) Sender \n2) Receiver");

			choice = input.nextInt();

			System.out.println("\nEnter the binary string.");

			message = input.next();

			while(message.length() % 4 != 0 && choice == 1){
				message = message + "0";
			}

			switch(method){
				case 1:
					if(choice == 1){
						result = sendChecksum(message);
					} else {
						result = receiveChecksum(message);
					}
					break;
				case 2:
					//
					if(choice == 1){
						result = sendCRC(message);
					} else {
						result = receiveCRC(message);
					}
					break;
				case 3:
					if(choice == 1){
						result = sendHamming(message);
					} else {
						result = receiveHamming(message);
					}
					break;
				default:
					break;
			}

			System.out.println(result);
			System.out.println();

		}

	}

	private static String sendChecksum(String message){
		AddFactory addFactory = new AddFactory(message);
		System.out.println("What would you like to add to it? ");
		addFactory.add(input.next());
		System.out.println("after adding: " + addFactory.getNum());

		return "";
	}

	private static String receiveChecksum(String message){
		return "";
	}

	private static String sendHamming(String message){
		return "";
	}

	private static String receiveHamming(String message){
		return "";
	}

	private static String sendCRC(String message){
		message = message + "000";

		System.out.print("Enter the generated polynomial: ");
		String poly = input.next();

		String remainder = getRemainder(new StringBuilder(message), poly);

		return xor(message, remainder);
	}

	private static String receiveCRC(String message){
		System.out.print("Enter the generated polynomial: ");
		String poly = input.next();

		StringBuilder remainder = new StringBuilder (getRemainder(new StringBuilder(message), poly));

		if(remainder.indexOf("1") == -1){
			return "No error detected";
		} else {
			return "Error detected.";
		}

	}


	private static String xor(String a, String b){
		while(a.length() < b.length()){
			a = "0" + a;
		}
		while(b.length() < a.length()){
			b = "0" + b;
		}

		StringBuilder result = new StringBuilder(a);

		for(int i = 0; i < b.length(); i++){
			if(a.charAt(i) != b.charAt(i)){
				result.setCharAt(i, '1');
			} else {
				result.setCharAt(i, '0');
			}
		}

		return result.toString();
	}





	//@require: dividend and divisor both start with '1'
	public static String getRemainder(StringBuilder dividend, String divisor){

		if(divisor.length() > dividend.length()){
			System.out.println("tried to divide bigger number into smaller number");
			return dividend.toString();
		}

		int xorHere = 0;

		while(xorHere < dividend.length() + 1 - divisor.length()){

			if(dividend.charAt(xorHere) == '1'){
				//xor it

				for(int i = 0; i < divisor.length(); i++){
					if(dividend.charAt(xorHere + i) == divisor.charAt(i)){
						//if they're the same, it's zero
						dividend.setCharAt(xorHere + i, '0');
					} else {
						//different characters, it's one
						dividend.setCharAt(xorHere + i, '1');
					}
				}
			}

			xorHere++;
		}

		return dividend.toString();

	}

}




class AddFactory{

	private StringBuilder num;

	public AddFactory(){
		num = new StringBuilder("0");
	}

	public AddFactory(String num){
		this.num = new StringBuilder(num);
	}

	public void add(String other){

		int shortest = other.length();

		if(num.length() < shortest){
			shortest = num.length();

			String str = num.toString();
			num = new StringBuilder(other);
			other = str;
		}

		int carry = 0;


		for(int i = 1; i <= shortest; i++){

			if(num.charAt( num.length() - i ) != other.charAt(other.length() - i)){
				if(carry == 0){
					num.setCharAt( num.length() - i, '1');
				} else {
					num.setCharAt( num.length() - i, '0');
				}
			} else if( num.charAt( num.length() - i ) == '1' && other.charAt(other.length() - i) == '1'){
				if(carry == 0){
					num.setCharAt( num.length() - i, '0');
					carry = 1;
				} else {
					num.setCharAt( num.length() - i, '1');
				}
			} else {
				if(carry == 0){
					num.setCharAt( num.length() - i, '0');
				} else {
					num.setCharAt( num.length() - i, '1');
				}
			}

		}

		if(carry == 1){
			if(num.length() == other.length()){
				String newNum = num.toString();
				newNum = "1" + newNum;
				num = new StringBuilder(newNum);
			} else if(num.length() > other.length()){

				// :shrug:
				String newOther = "1";
				for(int i = 0; i < other.length(); i++){
					newOther = newOther + "0";
				}

				System.out.println("things got complicated!!!");
				this.add(newOther);

			}
		}
	}



	public String getNum(){
		return num.toString();
	}


}