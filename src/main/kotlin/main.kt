import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.PdfStamper
import com.lowagie.text.xml.xmp.XmpWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


fun main(args: Array<String>) {
    val filesToEdit = getAllFilesToWorkWith()
    filesToEdit.forEach { file ->
        removeTitle(file)
    }
}

fun removeTitle(filePath : String){
    /**
     * Function editing the PDF metadata without overwriting it
     */
    try {
        //Opening the original file
        val reader = PdfReader(filePath)
        //Object editing the PDF
        val stamp = PdfStamper(
                reader,
                FileOutputStream(filePath.dropLast(4) + "2.pdf")
        )
        //Metadata array
        val mdata = reader.info as HashMap<String, String>
        //Setting title to ""
        mdata["Title"] = "";
        stamp.infoDictionary = mdata;
        //Saving the changes
        val baos = ByteArrayOutputStream()
        val xmp = XmpWriter(baos, mdata)
        xmp.close()
        stamp.setXmpMetadata(baos.toByteArray())
        stamp.close()
    }
    catch (e:IOException){
        println("IO Exception")
    }
}

fun getAllFilesToWorkWith():ArrayList<String>{
    /**
     * Function fetching every file to edit
     */
    val files = ArrayList<String>()
    fun iterateDir(dir : File){
        dir.listFiles() {file ->
            when {
                file.isDirectory -> iterateDir(file)
                file.name.contains("pdf") -> files.add(file.absolutePath)
                else -> println("Not a pdf : " + file.name)
            }
            true
        }
    }
    //Getting directory to work with
    val pwd = System.getProperty("user.dir")
    println("Veuillez entrer le chemin du dossier parent des fichiers PDF à éditer [$pwd] : ")
    var answer = readLine()
    if (answer == "") answer = pwd

    //Iterating through dir
    val dir = File(pwd)
    if (!dir.isDirectory) throw Exception("File is not a directory")
    iterateDir(dir)
    return files
}