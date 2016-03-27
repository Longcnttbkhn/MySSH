package chpt.myssh.server.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import chpt.myssh.share.Command;
import chpt.myssh.share.Package;

public class RemoteExecuteCmd extends ExecuteComand {
	private ObjectOutputStream output;
	private ObjectInputStream input;

	public RemoteExecuteCmd(ObjectInputStream input, ObjectOutputStream output) {
		this.output = output;
		this.input = input;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void exe(Command cmd) throws IOException, ClassNotFoundException {
		if (cmd.getCommandName().equals("get")) {
			if (cmd.getParameter(1) == null) 
				writeln("No file name");
			else 
				get(cmd.getParameter(1));
		} else 
			super.exe(cmd);
	}
	
	public void get(String path) throws IOException {
		String fullPath = root_directory + "/" + getUserPath(path);
		File fileRead = new File(fullPath);
		if (fileRead.exists()) {
			writeln("Downloading ... ");
			get(fileRead);
			writeln("Download commplete");
		}
	}
	
	public void get(File file) throws IOException {
		if (file.isFile()) {
			createFile(file.getName());
			InputStream read = new FileInputStream(file);
			int length;
			byte[] buffer = new byte[1024];
			while ((length = read.read(buffer)) > 0) {
				output.writeObject(new Package(length, buffer));
				output.reset();
				output.flush();
			}
			output.writeObject(new Package(-1, null));
			output.reset();
			output.flush();
			read.close();
		}
	}
	
	public void createFile(String fileName) throws IOException {
		Command cmd = new Command("createfile");
		cmd.addParameter(fileName);
		output.writeObject(cmd);
		output.flush();
	}
	
	@Override
	public void disconnect() throws IOException {
		Command command = new Command("logout");
		output.writeObject(command);
		output.flush();
	}
	
	@Override
	public void write(String message) throws IOException {
		Command command = new Command("write");
		command.addParameter(message);
		output.writeObject(command);
		output.flush();
	}
	@Override
	public void writeln(String message) throws IOException {
		Command command = new Command("writeln");
		command.addParameter(message);
		output.writeObject(command);
		output.flush();
	}
	
	
	@Override
	public Command read() throws IOException, ClassNotFoundException {
		Command commandSend = new Command("read");
		output.writeObject(commandSend);
		output.flush();

		Command commandReceive = (Command) input.readObject();
		return commandReceive;
	}
}
