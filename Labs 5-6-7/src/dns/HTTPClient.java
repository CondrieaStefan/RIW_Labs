package dns;


import javax.xml.crypto.URIReferenceException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class HTTPClient {
    private String _ipAddress;
    private URLFormater _urlFormater;

    private int nrOfRedirect = 0;

    private StringBuilder _httpRequest;

    public HTTPClient(URLFormater urlFormater, String ipAddress) {
        _ipAddress = ipAddress;

        _urlFormater = urlFormater;
    }

    private void buildHttpRequest(boolean isRobots) {
        _httpRequest = new StringBuilder();
        // first line
        _httpRequest.append("GET ");
        if (isRobots)
            _httpRequest.append("/robots.txt");
        else
            _httpRequest.append(_urlFormater.get_localPathStr());
        _httpRequest.append(" HTTP/1.1\r\n");

        // second line
        _httpRequest.append("Host: ");
        _httpRequest.append(_urlFormater.get_domain());
        _httpRequest.append("\r\n");

        // third line
        _httpRequest.append("User-Agent: CLIENTRIW\r\n");

        // forth line
        _httpRequest.append("Connection: close\r\n");

        // fifth line
        //_httpRequest.append("If-Modified-Since: "); // TO DO

        // sixth line
        _httpRequest.append("\r\n");
    }

    public boolean checkForRobosts() throws IOException {
        buildHttpRequest(true);
        Socket socket = new Socket(InetAddress.getByName(_ipAddress), _urlFormater.get_port());

        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        pw.print(_httpRequest);
        pw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("Disallow:"))
                if (line.contains(_urlFormater.get_localPathStr()))
                    return false;
        }
        return true;
    }

    public boolean sendRequest() throws IOException {
        buildHttpRequest(false);
        Socket socket = new Socket(InetAddress.getByName(_ipAddress), _urlFormater.get_port());

        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        pw.print(_httpRequest);
        pw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String t = br.readLine();
        try {
            if (t.contains("HTTP/1.1 301 Moved Permanently")) {
                nrOfRedirect++;
                if (nrOfRedirect > 6) {
                    throw new CustomException("Too many redirects!");
                }
                t = br.readLine();
                if (t.contains("Location")) {
                    final String separator = "://";
                    int index = 0;
                    StringBuilder newLocation = new StringBuilder();
                    boolean flag = false;
                    for (Character c : t.toCharArray()) {
                        if (index > separator.length()) {
                            if (c.equals('/'))
                                break;
                            newLocation.append(c);
                        }

                        if (c == separator.charAt(index))
                            index++;
                        else
                            index = 0;
                    }

                    if (index < separator.length()) {
                        throw new CustomException("Invalid Location Header!");
                    }

                    _urlFormater.set_domain(newLocation.toString());
                    return sendRequest();
                }
            } else if (!t.contains("HTTP/1.1 200 OK")) {
                throw new CustomException("Error request!");
            } else {
                boolean flag = false;
                _urlFormater.buildFolderPath();
                File output = new File(_urlFormater.get_domain() + _urlFormater.get_localPath() + "/" + _urlFormater.get_page());
                if (!output.exists())
                    output.createNewFile();

                BufferedWriter writer = new BufferedWriter(new FileWriter(output));
                while ((t = br.readLine()) != null) {
                    if (t.trim().isEmpty())
                        flag = true;
                    if (flag)
                        writer.write(t + "\r\n");
                }
                writer.close();
            }
            br.close();

        } catch (CustomException e) {
            File output = new File("error.txt");
            if (!output.exists())
                output.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(output));
            writer.write(e.getMessage() + "\r\n\r\n");
            writer.write(_httpRequest.toString());

            writer.write(t + "\r\n");
            while ((t = br.readLine()) != null) {
                if (t.trim().isEmpty())
                    break;
                writer.write(t + "\r\n");
            }
            writer.close();
            return false;
        }
        return true;
    }
}
