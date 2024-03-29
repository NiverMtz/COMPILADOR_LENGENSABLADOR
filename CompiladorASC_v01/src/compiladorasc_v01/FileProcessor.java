package compiladorasc_v01;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* @author Edward Hunter */
//ESTA CLASE SE UTILIZARÁ EN LA FUNCIÓN PRINCIPAL, PARA QUE PODEMOS PROCESAR SU ARCHIVO Y PROCESARLO DE ACUERDO CON SU FORMULARIO.
//THIS CLASS IS GOING TO BE USED AT THE MAIN FUNCT, SO WE CAN PROCESSS OUR FILE, AND PROCESS IT ACORDING TO ITS FORM.
public class FileProcessor {
    BufferedReader bufferFile;
    FileReader source;
    //CREA EL OBJETO DE LECTURA Y PROCESAMIENTO DE ARCHIVOS
    //CREATES THE OBJECT FOR FILE READING AND PROCESSING
    public FileProcessor(File sourceCode) throws FileNotFoundException {
      this.source=new FileReader(sourceCode);
      this.bufferFile = new BufferedReader(new FileReader(sourceCode));
    }
    //Aqui inicia el pre-procesado de las variables/constantes
    public void processBuffer() {
        try {
            String line;
            try (PrintWriter writer = new PrintWriter("temp.asc", "UTF-8")) {
                while ((line = this.bufferFile.readLine()) != null) {
                    writer.println(line.toUpperCase());
                }
            }
            this.bufferFile = new BufferedReader(this.source);
            while ((line = this.bufferFile.readLine()) != null) {
                String s = line;
                if (s.contains("EQU")) {
                    String[] words = s.split("\\W+");//Excluyendo todos los alfanumericos y el '_'
                    String key = words[0];
                    //String address = '$' + words[2];
                    String address = words[2];
                    System.out.println("REPLACING: " + key + " BY: " + address);
                    modifyFile(key, address);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            System.err.print("The file is damaged");
        }
    }
    //ESTE MÉTODO REEMPLAZA TODAS LAS PALABRAS CLAVE POR LA DIRECCIÓN DE CADA VARIABLE/CONSTANTE
    //THIS METHOD REPLACE ALL THE KEYWORDS BY THE ADDRESS OF EACH VARIABLE/CONSTANT
    static void modifyFile(String key, String address) throws FileNotFoundException, IOException {
        // Path path = Paths.get("/Users/UsuarioInvitado/pro.asc");
        Path path = Paths.get("temp.asc");
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll(key, "\\$" + address);
        Files.write(path, content.getBytes(charset));
        System.out.println("Done");
    } //Aqui termina el pre-procesado de las variables/constantes
    //Valida que dentro de el archivo fileMnemos exista el nemónico
    public int processBuffer_containsMnemo(String mnemo, File fileMnemos) throws FileNotFoundException {
        //Se crea una valiable local para el archivo porque sino marcaba error
        BufferedReader bufferFile_Mnemos = new BufferedReader(new FileReader(fileMnemos));
        try {
            String line;
            //while ((line = new BufferedReader(new FileReader(fileMnemos).readLine()) != null) { //La forma shida
            while ((line = bufferFile_Mnemos.readLine()) != null) {
                //Separa la linea cada que encuentra el símbolo "|" y los que está antes y después lo convierte en una posición del arreglo.
                String[] aux = line.split("[|]");
                if(mnemo.equals(aux[0])){
                    return 406;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            System.err.print("The file is damaged");
        }
        return 400;
    }
    /*
        Son los índices a los que correspone cada modo de ireccionamiento, una vez que se convierte
        la cadena del archivo 'file_1', que contiene todos los opCodes de cada modo de direccionamiento
        para cada mnemónico. El nemónico sirve como una llave para acceder a lo demás
    
        mnemo (Key)    = 0
        opCode (IMM)   = 1
        opCode (DIR)   = 2
        opCode (IND,X) = 3
        opCode (IND,Y) = 4
        opCode (EXT)   = 5
        opCode (INH)   = 6
        opCode (REL)   = 7 
    */
    /*Convierte en un array de String cada linea para poder acceder a cada índice como si fuera un diccionario y
    obtener el Opcode y los Bytes permitidos para cada modo por mnemónico*/
    public static String convertLineToArray(String line, int index_mod) {
        //Separa la linea cada que encuentra el símbolo "|" y los que está antes y después lo convierte en una posición del arreglo.
        String[] aux = line.split("[|]");
        //regresa la palabra en la posición especificada, tal que podemos acceder como si fuera un diccionario.
        return aux[index_mod];
    }
    /*
        Este método valida que al pasarle el nemónico que se está buscando, el archivo y un índice, tal que este
        último se refiere al índice del arreglo en que se convierte cada cadena que vamos leyendo del archivo que
        contiene los mnemónicos y sus respectivos opCode's por cada modo de direccionamiento
    */
    public String processBuffer_consult(String mnemo, File fileMnemos, int index) throws FileNotFoundException {
        String aux = null;//variable auxiliar
        try {
            String line;
            while ((line = this.bufferFile.readLine()) != null) {
                //valida que la llave o mnemónico sea igual al que se solicita en el argumento del método
                if (mnemo.equals(convertLineToArray(line, 0))) {
                    //Si es igual, guarda la palabra que correponda al modo de direccionamiento, según su índice en el arreglo.
                    aux = convertLineToArray(line, index);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            System.err.print("The file is damaged");
        }
        //retorno de variable auxiliar con la palabra de nuestro diccionario de mnemónicos y sus códigos.
        return aux;
    }
    //Este puede quedar inutlizado, pero valida se trata de convertir cada linea del archivo a un indice dentro de una lista
    public LinkedList manipularLineapoorLinea(LinkedList l) throws FileNotFoundException {
        File fileProcess = new File("temp.asc");
        BufferedReader bufferFile_Mnemos = new BufferedReader(new FileReader(fileProcess));
        try {
            String line;
            while ((line = bufferFile_Mnemos.readLine()) != null) {
                l.add(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            System.err.print("The file is damaged");
        }
        return l;
    }
}
