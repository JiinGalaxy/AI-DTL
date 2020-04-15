import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DTL{

    // Decision tree;
    static String[] header_word;
    public static void main(String[] args) {
        DTL cr = new DTL();
        if (args.length == 3) {
            // read files, and create different nodes;
            LinkedList<Node> training_data = cr.readLearningData(args[0]);
            // DTL
            BinNode decision_tree = new BinNode();
            decision_tree = decision_tree.DTL(training_data, Integer.parseInt(args[2]));

//          cr.buildModel(training_data, Integer.parseInt(args[2]));
            LinkedList<Node> testing_data = cr.readTestingData(args[1]);
            LinkedList<String> rating_result = new LinkedList<>();
            for(Node x : testing_data){
                rating_result.add(cr.predict(decision_tree,x));
            }
            for (String rate: rating_result) {
                System.out.println(rate);
            }
        }else {
// if the arguments is less than 3, we report there is a problem.
            System.out.println("not correct parameters");
        }
    }
    //  To test the DTL
    LinkedList<Node> readTestingData(String file) {
        LinkedList<Node> testing_data = new LinkedList<>();
        String[] valueHeader = new String[5];
        File f = new File(file);
        FileReader fr;
        try {
            fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int i = 0;
            while((line = br.readLine()) != null) {
                if(i == 0){
                    String[] read = line.split("\\s+");

                    for(int j = 0, k = 0; j<read.length; j++) {
                        if(read[j].isEmpty()) {
                            continue;
                        }else {
                            valueHeader[k] = read[j];
//                            header_word[k] = read[i];
                            k++;
                        }
                    }
                }
                if(i>0) {
                    // every line for one node;
                    String[] read = line.split("\\s+");
                    String[] infor = new String[5];
                    for(int j = 0, k = 0; j<read.length; j++) {
                        if(read[j].isEmpty()) {
                            continue;
                        }else {
                            infor[k] = read[j];
                            k++;
                        }
                    }
                    testing_data.add(new Node(valueHeader, infor));
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return testing_data;
    }
    LinkedList<Node> readLearningData(String file) {
        LinkedList<Node> learning_data = new LinkedList<>();
        String[] valueHeader = new String[6];
        File f = new File(file);
        FileReader fr;
        try {
            fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int i = 0;
            while((line = br.readLine()) != null) {
                if(i == 0){
                    String[] read = line.split("\\s+");

                    for(int j = 0, k = 0; j<read.length; j++) {
                        if(read[j].isEmpty()) {
                            continue;
                        }else {
                            valueHeader[k] = read[j];
//                            header_word[k] = read[i];
                            k++;
                        }
                    }
                }
                if(i > 0) {
                    // every line for one node;
                    String[] read = line.split("\\s+");
                    String[] infor = new String[6];
                    for(int j = 0, k = 0; j<read.length; j++) {
                        if(read[j].isEmpty()) {
                            continue;
                        }else {
                            infor[k] = read[j];
                            k++;
                        }
                    }
                    learning_data.add(new Node(valueHeader, infor));
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return learning_data;
    }
    /**
     * Algorithm 3 predict(n, data)
     * Require: Decision tree with root node n, data in the form of attribute values x.
     * 1: while n is not a leaf node do
     * 2: if x[n.attr] ≤ n.splitval then
     * 3: n ← n.lef t
     * 4: else
     * 5: n ← n.right
     * 6: end if
     * 7: end while
     * 8: return n.label.
     * */
    String predict(BinNode n, Node data){

        while(n.right != null || n.left != null){
            if(data.attr_val.get(n.key_val.keySet().iterator().next()) <= n.key_val.get(n.key_val.keySet().iterator().next())){
                n = n.left;
            }else{
                n = n.right;
            }
        }
        return n.mode.rating;
    }
}
class BinNode{
    int BNNo = 0;
    BinNode left;
    BinNode right;
    Node mode;
    Map<String, Double> key_val;


    public BinNode() {

        // TODO Auto-generated constructor stub
    }
    boolean attr_is_equal(Node[] source){
        int i = 1;
        while(i < source.length){
            if(!source[i].attr_val.toString().equalsIgnoreCase(source[i-1].attr_val.toString())){
                break;
            }
            i++;
        }
        return i==source.length;
    }
    boolean rating_is_equal(Node[] source){
        int i = 1;
        while(i < source.length){
            if(!source[i].rating.equalsIgnoreCase(source[i-1].rating)){
                break;
            }
            i++;
        }
        return i==source.length;
    }
    //   To build a binary tree to create a decision system;
    /**
     * Algorithm 1 DTL(data, minleaf)
     * Require: data in the form of N input-output pairs {xi, yi}N / i=1, minleaf ≥ 1.
     * 1: if (N ≤ minleaf) or (yi = yj for all i, j) or (xi = xj for all i, j) then
     * 2:   Create new leaf node n.
     * 3:   if there is a unique mode (most frequent value) in {yi}N / i=1 then
     * 4:       n.label ← mode in {yi}N i=1
     * 5:   else
     * 6:       n.label ← unknown
     * 7:   end if
     * 8:   return n.
     * 9: end if
     * 10: [attr,splitval] ← choose-split(data) --> BinNode splitData(Node[] data){...}
     * 11: Create new node n.
     * 12: n.attr ← attr
     * 13: n.splitval ← splitval
     * 14: n.lef t ← DTL(data with xi[attr] ≤ splitval, minleaf)
     * 15: n.right ← DTL(data with xi[attr] > splitval, minleaf)
     * 16: return n.
     */
    BinNode DTL(LinkedList<Node> learning_data, int minileaves) {
        BNNo++;
        String[] kvpair = new String[2];
        Node[] source = new Node[learning_data.size()];
        for(int i = 0; i < learning_data.size(); i++ ){
            source[i] = learning_data.get(i);
        }
        //
        // 1- 9;
        if (source.length <= minileaves || rating_is_equal((source))|| attr_is_equal(source)){
            BinNode leaf = new BinNode();
            leaf.mode = new Node();
            Node mode = leaf.mode;
            LinkedList<String> rate_details = new LinkedList<>();
            LinkedList<String> rate_base = new LinkedList<>();
            for (Node x : source
                 ) {
                rate_details.add(x.rating);
                if(!rate_base.contains(x.rating)){
                    rate_base.add(x.rating);
                }
            }
            // for (String x: rate_base
            //      ) {
            //     System.out.print(x + " ");
            // }
            // System.out.println(rate_base.size());

            // for (String x: rate_details
            // ) {
            //     System.out.print(x + " ");
            // }
            // System.out.println(rate_details.size());

            int[] rate_num = new int[rate_base.size()];
            for(int i = 0; i < rate_num.length; i++){
                for(int j = 0; j <  rate_details.size(); j++){
                    if(rate_base.get(i).equalsIgnoreCase(rate_details.get(j))){
                        rate_num[i]++;
                    }
                }
            }
            // for (int i: rate_num
            //      ) {
            //     System.out.print(i + " ");
            // }
            // System.out.println();

            int index = 0;
            for (int i = 0; i < rate_num.length; i++){

                if(rate_num[index] < rate_num[i]){
                    index = i;
                }
            }
            for (int i = 0; i < rate_num.length;i++){
                if(rate_num[index] == rate_num[i] && index != i){
                    index = -1;
                    break;
                }
            }

            // System.out.println(index);
            if(index != -1){
                mode.rating = rate_base.get(index);
                // System.out.println("result ---"+mode.rating);

            }else {
                mode.rating = "unknown" ;
                // System.out.println("cant find.");
            }
//            root.mode = mode;
            return leaf;
        }
        //* 10 to get a particular k-v pair
        LinkedList<String> key_val_pair = splitData(source);
        kvpair = key_val_pair.getFirst().split("#");

        // new leaves
        BinNode root = new BinNode();
//        root.key_val = attr_value;
        LinkedList<Node> left_brach = new LinkedList<>();
        LinkedList<Node> right_brach = new LinkedList<>();
        for(Node tmp : learning_data){
            if(tmp.attr_val.get(kvpair[0])-Double.parseDouble(kvpair[1])<=0){
                left_brach.add(tmp);
            }else {
                right_brach.add(tmp);
            }
        }
        root.key_val = new HashMap<>();
        root.key_val.put(kvpair[0],Double.parseDouble(kvpair[1]));
//        System.out.println("expend left with "+left_brach.size() + "if " + kvpair[0] + "<=" + kvpair[1]);
        root.left = DTL(left_brach,minileaves);
//        System.out.println("expend right with "+right_brach.size() + "if " + kvpair[0] + ">" + kvpair[1]);
        root.right = DTL(right_brach,minileaves);
        return root;
    }
    /**
     * Require: data in the form of N input output pairs {xi, yi}N i=1.
     *1: bestgain ← 0
     *2: for each attr in data do
     *3:    Sort the array x1[attr], x2[attr], . . . , xN [attr].
     *4:    for i = 1, 2, . . . , N − 1 do
     *5:        splitval ← 0.5(xi[attr] + xi+1[attr])
     *6:        gain ← Information gain of (attr, splitval) // See lecture slides.
     *7:        if gain > bestgain then
     *              bestgain <<<< gain;
     *8:            bestattr ← attr and bestsplitval ← splitval
     *9:        end if
     *10:   end for
     *11: end for
     *12: return (bestattr, bestsplitval).
     */
    LinkedList<String> splitData(Node[] data){
        LinkedList<String> key_val_pair = new LinkedList<>(); // the String type that need to return;
        String[] header_word = data[0].headers; // get the headers
        Map<String, Double>  bestGain = new HashMap();
        bestGain.put("",0.0);
        double best_gain = 0.0;
        String best_gain_header = "";
        double best_gain_value = 0.0;
        LinkedList<String> keyheaders = new LinkedList<>();
        for(String x : header_word){
            if(x.equalsIgnoreCase("Rating"))
                break;
            keyheaders.add(x);
        }
        // 5 attributes;
        for(String attribute : keyheaders){
            //sort data; Line 3
            if(attribute.equalsIgnoreCase("rating")){
                break;
            }
            data = sortArray(data,attribute);
            // *4-5;
            double split_line ;
            for(int i = 0; i < data.length - 1; i++){
//                System.out.println(i);
                split_line = (data[i].attr_val.get(attribute) + data[i+1].attr_val.get(attribute)) * 0.5;
                // calculate reminder;
                double info_gain = getInforGain(data, attribute, split_line);
//                System.out.println(info_gain);
                if(info_gain > best_gain ){
                    best_gain = info_gain;
                    best_gain_header = attribute;
                    best_gain_value = split_line;
                }
            }
//            System.out.println(best_gain + "----->" +attribute+": " + split_line);

        }
        String key_val = best_gain_header + "#" + best_gain_value;
//        System.out.println(key_val + ": " + best_gain);
        key_val_pair.add(key_val);
        return key_val_pair;
    }
    Node[] sortArray(Node[] arr, String attribute) {
        if (arr.length == 1) {
            return arr;
        } else {
            // seperate the array into 2 parts;
            int m = arr.length / 2;
            Node[] a1 = Arrays.copyOfRange(arr, 0, m);
            Node[] a2 = Arrays.copyOfRange(arr, m, arr.length);
            // recursion;
            a1 = sortArray(a1,attribute);
            a2 = sortArray(a2,attribute);
            return mergSort(a1, a2, attribute);
        }
    }
    Node[] mergSort(Node[] arr1, Node[] arr2, String attribute){
        Node[] array = new Node[arr1.length + arr2.length];
//      Index of the array;
        int i = 0;
//      Index of the arr1;
        int k = 0;
        // Index of the arr2
        int m = 0;
        while( k < arr1.length && m < arr2.length ){
            if (arr1[k].attr_val.get(attribute) < arr2[m].attr_val.get(attribute)) {
                array[i] = arr1[k];
                k++;
                i++;

            }else if(arr1[k].attr_val.get(attribute).equals(arr2[m].attr_val.get(attribute)) && arr1[k].rating.compareToIgnoreCase(arr2[m].rating) < 0){
                array[i] = arr1[k];
                k++;
                i++;
            }
            else{
                array[i] = arr2[m];
                m++;
                i++;
            }
        }
        while (k < arr1.length) {
            array[i] = arr1[k];
            k++;
            i++;
        }
        while(m < arr2.length){
            array[i] = arr2[m];
            m++;
            i++;
        }
        return array;
    }
    //  To test the d
    double getInforGain(Node[] data_source, String attribute, double splitLine){
        // calculate infor_ent
        double infor_ent = 0;
        double gain ;
        LinkedList<String> rating_collection = new LinkedList<>();
        for(int i = 0; i <  data_source.length; i++){
            if(!rating_collection.contains(data_source[i].rating)){
                rating_collection.add(data_source[i].rating);
            }
        }
        for(String rate : rating_collection){
            int rate_content_number =0;
            for(int i = 0; i< data_source.length; i++){
                if(data_source[i].rating.equalsIgnoreCase(rate)){
                    rate_content_number++;
                }
            }
            infor_ent += 0 - (double) rate_content_number / (double) data_source.length * Math.log((double) rate_content_number / (double) data_source.length);
        }
        infor_ent = infor_ent/Math.log(2.0);
        gain = infor_ent;
        infor_ent = 0;
        // split data_source by splitLine;
        LinkedList<Node> less_than_split = new LinkedList<>();
        LinkedList<Node> more_than_split = new LinkedList<>();
        for (Node candidate: data_source) {
            if(candidate.attr_val.get(attribute) <= splitLine){
                less_than_split.add(candidate);
            }else{
                more_than_split.add(candidate);
            }
        }
        // calculate less_than_split information entropy;
        rating_collection.clear();
        for(int i = 0; i <  less_than_split.size(); i++){
            if(!rating_collection.contains(less_than_split.get(i).rating)){
                rating_collection.add(less_than_split.get(i).rating);
            }
        }
        for(String rate : rating_collection){
            int rate_content_number =0;
            for(int i = 0; i< less_than_split.size(); i++){
                if(less_than_split.get(i).rating.equalsIgnoreCase(rate)){
                    rate_content_number++;
                }
            }
//            System.out.println("the less group possibility: in loop " + (double) rate_content_number / less_than_split.size());
            infor_ent += 0 - (double) rate_content_number / (double) less_than_split.size() * Math.log((double) rate_content_number / (double) less_than_split.size());
//            System.out.println("less than the split infor gain in for loop: " + infor_ent/Math.log(2.0));
        }
        infor_ent = infor_ent/Math.log(2.0);
//        System.out.println("less than the split infor gain: " + infor_ent);
        gain = gain - (double) less_than_split.size()/(double)  data_source.length * infor_ent;
        infor_ent = 0;
        // calculate more_than_split information entropy;
        rating_collection.clear();
        for(int i = 0; i <  more_than_split.size(); i++){
            if(!rating_collection.contains(more_than_split.get(i).rating)){
                rating_collection.add(more_than_split.get(i).rating);
            }
        }
        for(String rate : rating_collection){
            int rate_content_number =0;
            for(int i = 0; i< more_than_split.size(); i++){
                if(more_than_split.get(i).rating.equalsIgnoreCase(rate)){
                    rate_content_number++;
                }
            }
//            System.out.println(rate_content_number +"/"+ more_than_split.size());
//            System.out.println("the more group possibility: in loop " + (double) rate_content_number / more_than_split.size());
            infor_ent += 0 - (double) rate_content_number / more_than_split.size() * Math.log((double) rate_content_number / more_than_split.size());
//            System.out.println("more than the split infor gain in for loop: " + infor_ent/Math.log(2.0));

        }
        infor_ent = infor_ent/Math.log(2.0);
//        System.out.println("more than the split infor gian: " + + infor_ent);
        gain = gain - (double) more_than_split.size()/ (double) data_source.length * infor_ent;
        return gain;
    }
}
class Node{
    String[] headers = new String[6];
    HashMap<String, Double> attr_val;
    Set<String> node_data;
    double wc_ta;
    double re_ta;
    double ebit_ta;
    double mve_ta;
    double s_ta;
    String rating;
    Node(){

    }
    // when the programme create a node in this constructor,
    // the node have a set of key-value,and the attributes can be use directly.
    // I hope this is a strong node.
    Node(String[] key, String[] infor){
        headers = key;
        attr_val = new HashMap<>();
        if(infor.length == 6) {
            this.wc_ta = Double.parseDouble(infor[0]);
            this.re_ta = Double.parseDouble(infor[1]);
            this.ebit_ta = Double.parseDouble(infor[2]);
            this.mve_ta = Double.parseDouble(infor[3]);
            this.s_ta = Double.parseDouble(infor[4]);
            this.rating = infor[5];
            for(int i = 0; i < infor.length - 1; i++){
                attr_val.put(key[i], Double.parseDouble(infor[i]));
            }
            node_data = attr_val.keySet();
        }else if(infor.length == 5){
            this.wc_ta = Double.parseDouble(infor[0]);
            this.re_ta = Double.parseDouble(infor[1]);
            this.ebit_ta = Double.parseDouble(infor[2]);
            this.mve_ta = Double.parseDouble(infor[3]);
            this.s_ta = Double.parseDouble(infor[4]);
            for(int i = 0; i < infor.length; i++){
                attr_val.put(key[i], Double.parseDouble(infor[i]));
            }
            node_data = attr_val.keySet();
        }
    }
    // this node is creat for test the DT

}