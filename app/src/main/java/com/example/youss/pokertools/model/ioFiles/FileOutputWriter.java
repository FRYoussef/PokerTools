package model.ioFiles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileOutputWriter {
	private BufferedWriter writer; 
	
	public FileOutputWriter(String path) throws IOException{
		this.writer = new BufferedWriter(new FileWriter(path));
	}
	public FileOutputWriter(){}
	
	public void writeLine(String line) throws IOException{
		if(writer != null){
            this.writer.write(line);
            this.writer.newLine();
        }
	}
	
	public void closeWriter() throws IOException{
        if(writer != null)
		    this.writer.close();
	}
}
