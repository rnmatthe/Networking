//@author Rachel Matthews

import java.util.Scanner;
import java.util.ArrayList;

public class Homework1{
	
	private static Scanner input = new Scanner(System.in);

	private static int method;
	private static int choice;
	private static String message = "";
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
			System.out.println("1) Sender \n2) Receiver \n3) Back to main menu");

			choice = input.nextInt();

			if(choice == 1 || choice == 2){
				System.out.println("\nEnter the binary string.");
				message = input.next();
			} else {
				method = 4;
			}


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

		String result = addGroups(message);

		return message + flipBits(result);
	}

	private static String receiveChecksum(String message){
		
		String result = addGroups(message);
		result = flipBits(result);

		if( isZero( flipBits(result) ) ){
			System.out.println("Received message in error");
		} else {
			System.out.println("Correct message");
		}

		return "Computed checksum: " + result;
	}

	private static String sendHamming(String message){

		ArrayList<String> result = new ArrayList<>();
		ArrayList<Integer> powersOfTwo = new ArrayList<>();
		powersOfTwo.add(1);
		powersOfTwo.add(2);

		result.add("*");//place holder for zeroith index
		result.add("0");//place 1
		result.add("0");//place 2

		int mostRecentPower = 2;
		int messageIndex = 0;

		while(messageIndex < message.length()){
			if(result.size() == mostRecentPower * 2){
				result.add("0");
				mostRecentPower = mostRecentPower * 2;
				powersOfTwo.add(mostRecentPower);
			} else {
				result.add( "" + message.charAt(messageIndex) );
				messageIndex++;
			}
		}

		System.out.println();
		int newNum;
		int counter;
		int index;

		for(int i = 1; i < result.size(); i++){
			if(!powersOfTwo.contains(i)){
				//compute which powers of two take it into account
				newNum = i;
				counter = 0;

				for(int n = 0; newNum > 0; n++){
					if(newNum % 2 != 0){
						//xor with parity bit
						index = powersOfTwo.get(n);
						String xored = xor(result.get(index) , result.get(i));
						result.set(index, xored);
					}

					newNum = newNum / 2;
				}

			}
		}

		StringBuilder totalResult = new StringBuilder();

		for(int i = 1; i < result.size(); i++){
			totalResult.append(result.get(i));
		}

		return totalResult.toString();
	}

	private static String receiveHamming(String message){
		//output: "received message in error" or "correct message"
		// II:  If a single bit error, show the correct message (and information of which bit was flipped) 

		StringBuilder originalMessage = new StringBuilder(message);
		ArrayList<Integer> powersOfTwo = new ArrayList<>();
		powersOfTwo.add(1);
		powersOfTwo.add(2);

		StringBuilder theMessage = new StringBuilder(message);

		for(int i = 0; i < message.length(); i++){


			if( powersOfTwo.get(powersOfTwo.size() - 1) * 2 == i + 1){
				powersOfTwo.add(i + 1);
			} 

		}

		
		
		int newNum;
		int index;

		for(int i = 1; i < theMessage.length(); i++){
			if(!powersOfTwo.contains(i + 1)){
				//compute which powers of two take it into account
				newNum = i + 1;

				for(int n = 0; newNum > 0; n++){
					if(newNum % 2 != 0){
						//xor with parity bit
						index = powersOfTwo.get(n) - 1;
						String xored = xor("" + theMessage.charAt(index) , "" + theMessage.charAt(i));
						theMessage.setCharAt(index, xored.charAt(0));
					}

					newNum = newNum / 2;
				}

			}
		}

		boolean foundError = false;
		ArrayList<Integer> parityBitsInError = new ArrayList<>();

		for(int i = 0; i < powersOfTwo.size(); i++){
			if(theMessage.charAt( powersOfTwo.get(i) - 1) != '0'){
				foundError = true;
				parityBitsInError.add( powersOfTwo.get(i) - 1 );
			}
		}

		if(foundError){

			int flipIndex;

			System.out.println("Received message in error");

			if(parityBitsInError.size() == 1){

				flipIndex = parityBitsInError.get(0);
				//one parity bit in error - assume that bit was just in error, correct it

				if(originalMessage.charAt(flipIndex) == '0'){
					originalMessage.setCharAt(flipIndex, '1');
				} else {
					originalMessage.setCharAt(flipIndex, '0');
				}

				System.out.println("correct message: " + originalMessage.toString());

			} else {
				//more than one parity bit in error

				//if all parity bits have one message bit in common, flip the message bit
				//must not affect any other bits

				//all parity bits must affect that bit
				//no other parity bit must affect that bit

				ArrayList<Integer> effectAll = new ArrayList<>();
				boolean effectsAll = true;

				for(int i = 1; i <= message.length(); i++){

					effectsAll = true;

					for(int n = 0; n < parityBitsInError.size(); n++){
						if((i / (parityBitsInError.get(n) + 1)  ) % 2 != 1 ){
							effectsAll = false;
						}
					}

					if(effectsAll){
						effectAll.add(i);
					}
				}

				for(int i = 0; i < effectAll.size(); i++){
					//see if this affects other parity bits
					for(int n = 0; n < powersOfTwo.size(); n++){
						if(!parityBitsInError.contains(powersOfTwo.get(n) - 1) && effectAll.get(i) / powersOfTwo.get(n) % 2 == 1){
							effectAll.remove(effectAll.get(i));
						}
					}
				}

				if(effectAll.size() == 1){
					//flip just that message bit
					if(originalMessage.charAt(effectAll.get(0) - 1) == '0'){
						originalMessage.setCharAt(effectAll.get(0) - 1, '1');
					} else {
						originalMessage.setCharAt(effectAll.get(0) - 1, '0');
					}

					System.out.println("Correct message: " + originalMessage.toString());
					System.out.println("bit in error: " + effectAll.get(0));
				}

			}

			return "";
		} else {
			return "Correct message";
		}
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

		String remainder = getRemainder(new StringBuilder(message), poly);

		if(isZero(remainder)){
			return "No error detected";
		} else {
			return "Error detected.";
		}

	}


	private static boolean isZero(String num){
		StringBuilder strB = new StringBuilder(num);
		if(strB.indexOf("1") == -1){
			return true;
		} else {
			return false;
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


	private static String addGroups(String message){

		AddFactory addFactory = new AddFactory();

		for(int i = 0; i < message.length() / 4; i++){
			addFactory.add(message.substring(i * 4, i * 4 + 4));
		}

		String overflow;
		String base;
		while(addFactory.getNum().length() > 4){
			overflow = addFactory.getNum().substring(0, addFactory.getNum().length() - 4 );
			base = addFactory.getNum().substring(addFactory.getNum().length() - 4, addFactory.getNum().length());

			addFactory.setNum(base);
			addFactory.add(overflow);
		}

		return addFactory.getNum();
	}

	private static String flipBits(String str){
		StringBuilder result = new StringBuilder(str);

		for(int i = 0; i < result.length(); i++){
			if(result.charAt(i) == '0'){
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
			return "1";
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


//----------------------------------------------------------------------------------------


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
					carry = 0;
				}
			}

		}

		if(carry == 1){
			if(num.length() == other.length()){
				String newNum = num.toString();
				newNum = "1" + newNum;
				num = new StringBuilder(newNum);
			} else if(num.length() > other.length()){

				String newOther = "1";
				for(int i = 0; i < other.length(); i++){
					newOther = newOther + "0";
				}

				this.add(newOther);

			}
		}
	}

	public String getNum(){
		return num.toString();
	}

	public void setNum(String num){
		this.num = new StringBuilder(num);
	}


}