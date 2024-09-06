package comp;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Codenomalizer {
    public static void main(String[] args) {
        String usrdir = System.getProperty("user.dir");
        String dir = usrdir + "\\dataset";// 対象のディレクトリ
        String saveDir = "comp_dataset";//保存先のディレクトリ
        boolean testmode = false;
        boolean lineBreak = false;

        //-------------ディレクトリ内jsファイル名の取得----------
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File file, String str) {
                return str.endsWith(".js");
            }
        };
        File[] filelist = new File(dir).listFiles(filter); //jsファイル名リストを取得
        System.out.println("filelist");


        //-------------ファイルがあるか確認--------------------
        if(filelist == null ){
            System.err.println("ファイルがありません");
            System.exit(401);
        }

        for (File filename : filelist) {
            String PATH = filename.toString(); // 対象のファイルパス
            Path path = Paths.get(PATH); // ファイルパスを取得

            String target = filename.toString();

            ///-------------------------------------------------------------------------
            ///
            target=target.replaceAll(".+_", "");//ここでターゲットの変数名をとりだそうーｐ
            target=target.replaceAll(".js", "");
            ///
            ///-------------------------------------------------------------------------
            
            try {
                //checkStatementはifとfunctionは1、forなら2
                List<String> souce = Files.readAllLines(path, StandardCharsets.UTF_8);
                List<String> compSouce = new ArrayList<>(); //置き換えたファイルの保存先
                List<Integer> checkStatement = new ArrayList<>();//条件の種類のスタック
                System.out.println(filename); //ファイル名の表示
                int row = 0;//現在の行数
                int linescount = souce.size(); // 行数を取得

                
                while(true){
                    //-----------------------デバッグ------------------------
                    // System.out.println();
                    // System.out.println();
                    
                    String line = souce.get(row);

                    if (!(line.contains("{") && line.contains("}"))) {
                        //--------------------{}が同じ行じゃないとき---------------------
                        if (line.contains("{")) {
                            if(line.contains(target)){
                                checkStatement.add(0);
                                compSouce.add(line);
                            }else if (line.contains("if")) {
                                if(line.contains("else")){
                                    checkStatement.add(4);
                                }else{
                                    checkStatement.add(3);
                                }
                                compSouce.add("(");
                            }else if (line.contains("for")) {
                                checkStatement.add(2);
                                compSouce.add("(");
                            }else if (line.contains("function")) {
                                checkStatement.add(1);
                                compSouce.add("(");
                            }else{
                                checkStatement.add(1);
                                compSouce.add("(");
                            }
                            
                        }else if (line.contains("}")) {
                            if (checkStatement.size()-1 == 0) {
                                compSouce.add(")");
                            }else if (checkStatement.size()-1 == 1) {
                                compSouce.add(")");
                            }else if (checkStatement.size()-1 == 2) {
                                compSouce.add(")*");
                            }else if (checkStatement.size()-1 == 3) {
                                compSouce.add(")+ε");
                            }else if (checkStatement.size()-1 == 4) {
                                compSouce.add(")");
                            }
                            checkStatement.remove(checkStatement.size()-1);
                        }else{
                            compSouce.add(line);
                        }
                    }

                    

                    
                    //-----------------------終了条件---------------------------
                    row++;
                    if(linescount <= row)break;
                }
                //---------------結果の出力-------------------
                System.out.println(compSouce);

                //ファイル保存
                filewriter(usrdir,saveDir,filename,compSouce,lineBreak);
                
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }



    static void filewriter(String usrdir,String saveDir,File file,List<String> souce,boolean lineBreak){
        try {
            String filename = file.getName();
            filename = filename.replace(".js", "_comp"+".js");
            Path targetDir = Paths.get(usrdir+"\\"+saveDir);
            Path targetFile = Path.of(usrdir+"\\"+saveDir+"\\"+filename);

            //ファイルが存在するか確認してあるなら削除
            if (!Files.exists(targetDir))Files.createDirectories(targetDir);
            if(Files.exists(targetFile))Files.delete(targetFile);

            //ファイルの書き込み
            String tmpLine = "";
            if(lineBreak){
                for(String compline : souce){
                    tmpLine += compline + "\n";
                }
            }else{
                for(String compline : souce){
                    tmpLine +=compline;
                    tmpLine=tmpLine.replace(" ", "");
                }
            }
            Files.writeString(targetFile, tmpLine, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
