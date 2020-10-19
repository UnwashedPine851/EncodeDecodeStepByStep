package br.unisinos.encodedecodestepbystep.controller;

import br.unisinos.encodedecodestepbystep.controller.response.CodificationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class EliasGammaControllerTest {

    @Autowired
    EliasGammaController eliasGammaController;

    private InputStream isEsperadoBeforeCodification;
    private InputStream isCodewordEsperado;


    @BeforeEach
    void setUp() throws FileNotFoundException {
        this.isEsperadoBeforeCodification = new FileInputStream(new File("src/test/resources/filesToEncodeDecodeTest/alice29.txt"));
        this.isCodewordEsperado = new FileInputStream(new File("src/main/resources/database/CodewordsSizesArray.repository"));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.isEsperadoBeforeCodification.close();
        this.isCodewordEsperado.close();
    }

    @Test
    void deveSerOsMesmosCodewordsGravadosNoEncodeNoNextStepConcatenadoExcetoPeloCabecalhoQuandoEstiverNoProcessoDeEncode() throws InterruptedException, IOException {
        StringBuilder codewordEsperado = new StringBuilder("");
        eliasGammaController.encode("src\\test\\resources\\filesToEncodeDecodeTest\\alice29.txt");

        TimeUnit.SECONDS.sleep(10); // para dar tempo para iniciar thread do encode
        this.isCodewordEsperado.skip(17); // para skippar cabeçalho + virgula
        CodificationDTO codificationDTORetornado = eliasGammaController.nextStep();
        while (!codificationDTORetornado.getStepsFinished()) {
            while (codewordEsperado.length() != codificationDTORetornado.getCodeword().length()) {
                codewordEsperado.append((char) this.isCodewordEsperado.read());
            }
            this.isCodewordEsperado.read(); // para jogar o caracter da virgula fora.

            Assertions.assertEquals((char) this.isEsperadoBeforeCodification.read(), codificationDTORetornado.getCharacterBeforeCodification().charAt(0));
            Assertions.assertEquals(codewordEsperado.toString(), codificationDTORetornado.getCodeword());

            codewordEsperado = new StringBuilder("");
            codificationDTORetornado = eliasGammaController.nextStep();
        }
    }

    @Test
    void deveSerOsMesmosCodewordsGravadosNoDecodeNoNextStepConcatenadoExcetoPeloCabecalhoQuandoEstiverNoProcessoDeDecode() {
        //TODO
    }

    @Test
    void getProgressPercentage() {
        //TODO
    }
}