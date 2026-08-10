import examples.api.ApiServer;

public class ApiMain {

    public static void main(String[] args) throws Exception {

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;

        ApiServer.start(port);
    }
}