import java.io.*;
import java.net.*;

//java webserver 8817

public class WebServer{
  public static int port;
  public static String rootPath;

  private static InputStream is;
  private static OutputStream os;
  private static ServerSocket welcomeSocket;
  private static Socket connectionSocket;
  private static DataOutputStream outToClient;
  private static BufferedReader inFromClient;

  private static HTTPRequest requestObject;

  public static void main(String[] args) throws Exception {
    ServerSocket serverSocket;
    String clientSentence;
    String capitalizedSentence;

    //get port number and root path for file from command line
    System.out.println("=========Parsing Command Line ==========");
    if(args.length > 0){
      try{
        port =  Integer.parseInt(args[0]);
        System.out.println("Runing server on port: " + port);
        commandUrl = args[1];
        System.out.println("Root Path: " + rootPath);
      }catch(Exception e){
        System.err.println("ERROR:Unable to get information from command line");
        return;
      }
    }

    //start server to listen on port
    try{
      welcomeSocket = new ServerSocket(port);
      System.out.println("=========Start Server listening on port=========");
    }catch (IOException e){
      System.err.println("ERROR:Unable to listen on port");
      return;
    }


    while(true){
      try{
        connectionSocket = welcomeSocket.accept();
        inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        outToClient = new DataOutputStream(connectionSocket.getOutputStream());

        //read all request lines, concadinate into clientSentence
        clientSentence = inFromClient.readLine();
        while(!inFromClient.readLine().equals("")){
          clientSentence = clientSentence + "\n" + inFromClient.readLine();
        }
        //System.out.println(clientSentence);

        //feed clientsentence to httprequest, parseing request line
        requestObject = new HTTPRequest(clientSentence,commandUrl);

      }catch(IOException e){
        System.err.println("Unable to accept connection socket");
        continue;
      }
        connectionSocket.close();
    }

  }

}

class HTTPRequest{
  private static String rootPath;
  private static String requestCommand;
  private static String absPath;
  private static String contentType;

  private static String ifModifiedSince;
  private static Date dateIfModified;

  private static boolean ifError;
  private static String ErrorCode;
  public HTTPRequest(String requestFromSocket, String rootPath){
    System.out.println("========In HTTPRequest constructer===== \n");

    String lines[] = requestFromSocket.split("\n");
    String tokens[] = lines[0].split(" ");

    requestCommand = tokens[0];
    absPath = tokens[1];
    System.out.println("command: " + requestCommand);
    System.out.println("absPath: " + absPath);


    for(int i = 1; i < lines.length;i++){
      if(lines[i].indexOf("If_Modified_Since:")!= -1){
        System.out.println(lines[i]);
        ifModifiedSince = lines[i][18:]
      }
    }

    String pattern = "EEE MMM d hh:mm:ss zzz yyyy"
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    dateIfModified = simpleDateFormat.parse(ifModifiedSince)

    ifError = checkErrorCode();

  }

  private Boolean checkErrorCode(){
    if(requestCommand != "GET" || requestCommand != "HEAD"){
      ErrorCode = "501 Not Implemented";
      return true;
    }
    if(requestCommand == null || absPath == null){
      ErrorCode = "400 Bad Request";
      return true;
    }
    return false;
  }

  public String getRuestCommand(){
    return requestCommand;
  }
  public String getAbsPath(){
    return absPath;
  }
  public Date getIfModfied(){
    return dateIfModified;
  }
  public String getErrorCode(){
    return ErrorCode;
  }
  public Boolean getErrorFlag(){
    return ifError;
  }




}
