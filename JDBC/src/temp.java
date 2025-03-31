//import java.util.ArrayList;
//
//public class temp {
//    class Stacks<T>{
//        T[] arrayList ;
//
//        Stacks(int size){
//            arrayList =  (T[])new Object[size];
//        }
//        public void addToStack(T item){
//            arrayList.add(item);
//        }
//
//        public T peek(){
//            if(arrayList.isEmpty()) {
//                System.out.println("NO Element");
//            }
//            return arrayList.get(arrayList.size() - 1);
//        }
//
//        public void pop(){
//            if(arrayList.isEmpty()) {
//                System.out.println("NO Element");
//            }
//            else {
//                arrayList.remove(arrayList.size() - 1);
//            }
//
//        }
//
//
//    }
//
//    public static void main(String[] args) {
//        temp t1 = new temp();
//        Stacks <Integer> ss = t1.new Stacks<>();
//
//        ss.addToStack(1);
//        ss.addToStack(2);
//        ss.addToStack(3);
//        ss.addToStack(4);
//
//        System.out.println(ss.peek());
//
//    }
//}
