package comp;

import java.util.ArrayList;
import java.util.List;

/*
 * javascriptの変数の型はlet,const,varの3つがある
 * varと;で検索をかけて行を特定
 * =がある場合とない場合で削除する文字列が異なる
 */

public class searchVarName {
    public static List<String> searchVar(List<String> lines){

        List<String> varList = new ArrayList<>();//変数名リスト

        for(String line : lines){
            if(line.contains("if")||line.contains("for"))continue;
            if(line.contains("let ")){
                varList.add(deleteWithoutVar(line,"let"));
            }
            if(line.contains("var ")){
                varList.add(deleteWithoutVar(line,"var"));
            }
            if(line.contains("const ")){
                varList.add(deleteWithoutVar(line,"const"));
            }
        }
        return varList;
    }

    static String deleteWithoutVar(String line,String typeString){
        // line=line.replaceAll("= *\"* *([0-9]|[a-z]|[A-Z])* *\"* *","");
        line=line.replaceAll("=.*","");
        line=line.replaceAll(";", "");
        typeString="^.*"+typeString+" *";
        // line=line.replaceAll("^[a-z]* *", "");
        line=line.replaceAll(typeString, "");
        line=line.replaceAll(" ", "");

        // System.out.println(line);

        return line;
    }
}
