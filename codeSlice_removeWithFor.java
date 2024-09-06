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

public class codeSlice_removeWithFor {
public static void main(String[] args) throws Exception {

        String usrdir = System.getProperty("user.dir");
        String dir = usrdir + "\\src\\codeslice";// 対象のディレクトリ
        String saveDir = "dataset";//保存先のディレクトリ
        boolean testmode = false;
        String testVar = "brickHeight";



        //-------------ディレクトリ内jsファイル名の取得----------
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File file, String str) {
                return str.endsWith(".js");
            }
        };

        File[] list = new File(dir).listFiles(filter); //jsファイル名リストを取得


        //-------------ファイルがあるか確認--------------------
        if(list == null ){
            System.err.println("ファイルがありません");
            System.exit(401);
        }

        //---------------ファイルの読み込み-------------------
        for (File list1 : list) {
            String PATH = list1.toString(); // 対象のファイルパス
            Path path = Paths.get(PATH); // ファイルパスを取得
            
            try {
                List<String> souce = Files.readAllLines(path, StandardCharsets.UTF_8);
                List<String> lines = new ArrayList<>(souce); //ファイルの内容を取得
                System.out.println(list1); //ファイル名の表示

                //---------------変数名リストの取得-------------------
                searchVarName search = new searchVarName();
                List<String> varNameList;
                if(testmode==false){
                    varNameList = search.searchVar(lines);
                }else{
                    varNameList =new ArrayList<>();
                    varNameList.add(testVar);
                }
                System.out.println(varNameList);

                for(String target : varNameList){
                    //String target = "paddleX";  // 対象の変数名
                    boolean Processed = false; //処理が行われたかのフラグ
                    lines = new ArrayList<>(souce);
                    int row = 0;//現在の行数
                    int linescount = lines.size(); // 行数を取得
                    ArrayList<Integer> queue = new ArrayList<>(); // {}の開始行数を格納するスタック
                    ArrayList<Integer> checkArray = new ArrayList<>();
                    ArrayList<Integer> targetInFor = new ArrayList<>();
                    
                    while(true){
                        //-----------------------デバッグ------------------------
                        // System.out.println();
                        // System.out.println(lines.get(row));
                        // System.out.println(isthere);
                        //System.out.println(targetInFor);
                        // System.out.println(checkArray);
                        // System.out.println("queuecount : " + queueCount);
                        // System.out.println("row : "+ row);
                        // System.out.println(queue);
                        // System.out.println();
                        

                        if (!(lines.get(row).contains("{") && lines.get(row).contains("}"))) {
                            //--------------------{}が同じ行じゃないとき---------------------
                            if (lines.get(row).contains("{")) {
                                queue.add(row);
                                if(lines.get(row).contains(target)){
                                    checkArray.add(1);
                                    // for (int i=0;i<targetInFor.size();i++) {
                                    //     targetInFor.set(i, 1);
                                    // }
                                    targetInFor.add(1);
                                }else{
                                    if(!checkArray.isEmpty()){
                                        if (checkArray.get(checkArray.size()-1) == 1){
                                            checkArray.add(1);
                                        }else{
                                            checkArray.add(0);
                                        }
                                    }else{
                                        checkArray.add(0);
                                    }
                                    targetInFor.add(0);
                                }
                            }else if (lines.get(row).contains("}")) {
                                
                                // if(checkArray ==null || checkArray.isEmpty())continue;
                                if (checkArray.get(checkArray.size()-1) == 0) {
                                    int delRow = queue.get(queue.size()-1);
                                    // System.out.println("del this line: \n"+lines.get(row));
                                    lines.remove(row);
                                    // System.out.println("del this line: \n"+lines.get(delRow));
                                    lines.remove(delRow);
                                    row--;
                                    Processed = true;
                                    linescount-=2;
                                }

                                checkArray.remove(checkArray.size()-1);
                                queue.remove(queue.size()-1);
                                targetInFor.remove(targetInFor.size()-1);

                                if(Processed == false){
                                    if(!checkArray.isEmpty()){checkArray.set(checkArray.size()-1, 1);}
                                }


                            }else{
                                if(!lines.get(row).contains(target)){
                                    boolean x = false;
                                    for(int check : targetInFor){
                                        if(check == 1){
                                            x = true;
                                        }
                                    }
                                    if(targetInFor ==null || targetInFor.isEmpty()){
                                        lines.remove(row);
                                        Processed = true;
                                        linescount--;
                                    }else if (x==false){
                                        lines.remove(row);
                                        Processed = true;
                                        linescount--;
                                    }
                                }else if (lines.get(row).contains(target)){
                                    for (int i=0; i<checkArray.size(); i++){
                                        checkArray.set(i, 1);
                                    }
                                }
                            }
        
                        }else{
                            //{}が同じ行ならtargetで判断し削除
                            if(!lines.get(row).contains(target)){
                                if(checkArray ==null || checkArray.isEmpty()){
                                    lines.remove(row);
                                    Processed = true;
                                    linescount--;
                                }else if (checkArray.get(checkArray.size()-1) == 0){
                                    lines.remove(row);
                                    Processed = true;
                                    linescount--;
                                    
                                }
                            }
                        }

                        

                        
                        //-----------------------終了条件---------------------------
                        if(Processed != true){row++;}
                        Processed = false;
                        if(linescount <= row)break;
                        
                        //------------------------エラー回避--------------------------
                        if(row <= 0) {
                            // System.out.println("row is reseted"); 
                            row = 0;
                        }

                        // if(queueCount <= 0){
                        //     // System.out.println("queuecount is reseted");
                        //     queueCount=0;
                        // }

                        

                    }
                    //---------------結果の出力-------------------
                    //System.out.println(lines);

                    //---------------ファイルの書き込み-------------------
                    String filename = list1.getName();
                    filename = filename.replace(".js", "_"+target+".js");
                    Path targetDir = Paths.get(usrdir+"\\"+saveDir);
                    Path targetFile = Path.of(usrdir+"\\"+saveDir+"\\"+filename);

                    //ファイルが存在するか確認してあるなら削除
                    if (!Files.exists(targetDir))Files.createDirectories(targetDir);
                    if(Files.exists(targetFile))Files.delete(targetFile);

                    //ファイルの書き込み
                    String tmpLine = "";
                    for(String compline : lines){
                        tmpLine += compline + "\n";
                    }
                    Files.writeString(targetFile, tmpLine, StandardCharsets.UTF_8);

                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        
    }
}
