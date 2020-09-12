package codifications;

import utils.MathUtils;
import utils.StringUtils;
import utils.Writer;
import utils.Reader;
import java.io.*;
import java.util.Arrays;

import static codifications.Constants.*;

public class Goulomb implements Codification {

    private static final byte STOP_BIT = 1;

    private int divisor; //K

    public Goulomb(int divisor){
        this.divisor = divisor;
    }

    public void encode(File file) throws IOException {

        Reader reader = new Reader(file);
        Writer writer = new Writer(ENCODED_FOLDER+file.getName()+EXTENSION);
        String bits = "";

        int character = 0;
        while((character=reader.read())!=-1){
            int restOfDivision = character % this.divisor;
            int digitsToRepresentTheRest = MathUtils.logBase2(this.divisor);
            String restBinary = StringUtils.integerToStringBinary(restOfDivision, digitsToRepresentTheRest);

            int division = character / this.divisor;
            String zeros = StringUtils.createStreamOnZeros(division);
            String codewards = zeros + STOP_BIT + restBinary;
            bits = bits.concat(codewards);
            while (bits.length() > 8){
                writer.write(bits.substring(0,8));
                bits = bits.substring(8);
            }
        }
        if(bits.length() != 0){
            writer.write(bits);
        }
        writer.close();
        reader.close();
    }

    public void decode(File file) throws IOException {
        Reader reader = new Reader(file);
        Writer writer = new Writer(DECODED_FOLDER+file.getName());

        boolean alreadyFoundStopBit = false;

        int digitsOnRest = MathUtils.logBase2(this.divisor);
        int quocient =  0;
        String binary = reader.readBytes();

        for (int count = 0; count < binary.length(); count++) {
            char character = binary.charAt(count);
            if(!alreadyFoundStopBit){
                if((character-'0') == STOP_BIT){
                    alreadyFoundStopBit = true;
                }else{
                    quocient++;
                }
            }else{
                String restInBinary = "";
                restInBinary+=character;
                for (int i =1; i<digitsOnRest; i++){
                    restInBinary+=binary.charAt(++count)-'0';
                }

                int rest = Integer.parseInt(restInBinary,2);
                char finalNumber = (char) ((quocient * this.divisor) + rest);
                writer.write(finalNumber);
                quocient = 0;
                alreadyFoundStopBit = false;
            }
        }
        writer.close();
        reader.close();
    }
}
