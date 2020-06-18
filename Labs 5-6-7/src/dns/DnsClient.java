package dns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DnsClient {
    private int _dnsServerPort;
    private String _ipAddr;
    private String _domain;

    public DnsClient(String domain, String ipAddr, int dnsServerPort) {
        _domain = domain;
        _ipAddr = ipAddr;
        _dnsServerPort = dnsServerPort;
    }
    public String getIpAddres() throws IOException {
        InetAddress ipAddress = InetAddress.getByName(_ipAddr);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeShort(0x1234);

        // Write Query Flags
        dos.writeShort(0x0100);

        // Question Count: Specifies the number of questions in the Question section of the message.
        dos.writeShort(0x0001);

        // Answer Record Count: Specifies the number of resource records in the Answer section of the message.
        dos.writeShort(0x0000);

        // Authority Record Count: Specifies the number of resource records in the Authority section of
        // the message. (“NS” stands for “name server”)
        dos.writeShort(0x0000);

        // Additional Record Count: Specifies the number of resource records in the Additional section of the message.
        dos.writeShort(0x0000);

        String[] _domainParts = _domain.split("\\.");

        for (int i = 0; i<_domainParts.length; i++) {
            byte[] _domainBytes = _domainParts[i].getBytes("UTF-8");
            dos.writeByte(_domainBytes. length);
            dos.write(_domainBytes);
        }

        // No more parts
        dos.writeByte(0x00);

        // Type 0x01 = A (Host Request)
        dos.writeShort(0x0001);

        // Class 0x01 = IN
        dos.writeShort(0x0001);

        byte[] dnsFrame = baos.toByteArray();

        // *** Send DNS Request Frame ***
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket dnsReqPacket = new DatagramPacket(dnsFrame, dnsFrame.length, ipAddress, _dnsServerPort);
        socket.send(dnsReqPacket);

        // Await response from DNS server
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);


        DataInputStream din = new DataInputStream(new ByteArrayInputStream(buf));
        short id = din.readShort();
        short flags = din.readShort();
        short questions = din.readShort();
        short answers = din.readShort();
        short authority = din.readShort();
        short additional = din.readShort();

        int recLen = 0;
        while ((recLen = din.readByte()) > 0) {
            byte[] record = new byte[recLen];

            for (int i = 0; i < recLen; i++) {
                record[i] = din.readByte();
            }
        }

        short recordType = din.readShort();
        short clas = din.readShort();
        short fields = din.readShort();
        short type = din.readShort();
        short clas2 = din.readShort();
        int ttl = din.readInt();

        short addrLen = din.readShort();

        StringBuilder ipAddres = new StringBuilder();

        for (int i = addrLen - 4; i < addrLen; i++ ) {
            ipAddres.append(String.format("%d", (din.readByte() & 0xFF)));
            if (i != addrLen - 1) {
                ipAddres.append('.');
            }
        }
        socket.close();

        return ipAddres.toString();
    }
}