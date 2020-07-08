//import static fj.data.List.list;

import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import fj.*;
import fj.data.*;
import java.io.*;
import fj.data.vector.*;
import fj.function.Effect1;
import fj.control.parallel.*;

public class SeminarApp {
    public static void main(final String[] args) {
        //functions();
        //functionalJavaFunctions();
        //destructiveFuntions();
        //ioExample();
        unitExample();
    }

    public void immutableExample() {
        //Create List
        List<String> modifiablelist = new ArrayList<String>();
        modifiablelist.add("testvalue");

        //Create Immutable List
        List<String> immutablelist = Collections.unmodifiableList(modifiablelist);
        immutablelist.add("exception should be thrown");

        System.out.println("Java modifiable List: " + modifiablelist);
        // Modify mutable List
        modifiablelist.set(0, "modified testvalue");
        // Unmodifiable List has changed too
        System.out.println("Unmodifiable List: " + immutablelist); 
    }

    public static void functions() {
        BiFunction<Integer, Integer, Integer> constructFunction = (x, y) -> x + y;
        System.out.println(constructFunction.apply(5, 10));

        Function<Integer, Function<Integer, Integer>> destructiveFunction = x -> y -> x + y;
        System.out.println(destructiveFunction.apply(5).apply(10));

        Function<Integer, Integer> sum10 = destructiveFunction.apply(10);
        Function<Integer, Integer> sum20 = destructiveFunction.apply(20);

        System.out.println(sum10.apply(5)); // 15
        System.out.println(sum20.apply(5)); // 25
    }

    public static void functionalJavaFunctions() {

        F0<String> arity0 = () -> "examplestring";
        System.out.println(arity0.f());

        F2<Integer, Integer, Integer> arity2 = (a,b) -> a + b;
        System.out.println(arity2.f(1, 2));

        // is the same as following
        BiFunction<Integer, Integer, Integer> biFunction = (a,b) -> a + b;
        System.out.println(biFunction.apply(1, 2));


        F8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> arity8 =
            (a, b, c, d, e, f, g, h) -> a + b + c + d + e + f + g + h;
        System.out.println(arity8.f(1, 2, 3, 4, 5, 6 ,7 ,8));
    }

    public static void destructiveFuntions() {
        Function<Integer, Function<Integer, Function<Integer, Integer>>> arity3 = a -> b -> c -> a + b + c;
        Function<Integer, Function<Integer, Integer>> arity2 = arity3.apply(1);
        Function<Integer, Integer> arity1 = arity2.apply(2);
        System.out.println(arity1.apply(3));

        Function<Integer,
            Function<Integer,
                Function<Integer,
                    Function<Integer,
                        Function<Integer,
                            Function<Integer,
                                Function<Integer,
                                    Function<Integer, Integer>>>>>>>> arity8 =
                                        a -> b -> c -> d -> e -> f -> g -> h -> a + b + c + d + e + f + g + h;
        System.out.println(arity8.apply(1).apply(2).apply(3).apply(4).apply(5).apply(6).apply(7).apply(8));

    }

    public static void ioExample() {

        final IO<Unit> askName = () -> {
            System.out.println("Hi, what's your name?");
            return Unit.unit();
        };
        IO<Unit> promptName = IOFunctions.stdoutPrint("Name: ");
        IO<Unit> askAndPromptName = IOFunctions.append(askName, promptName);

        final IO<String> readName = () -> new BufferedReader(new InputStreamReader(System.in)).readLine();
        final F<String, IO<Unit>> upperCaseAndPrint = F1Functions.<String, IO<Unit>, String>o(IOFunctions::stdoutPrint).f(String::toUpperCase);
        final IO<Unit> readAndPrintUpperCasedName = IOFunctions.bind(readName, upperCaseAndPrint);

        final IO<Unit> program = IOFunctions.append(askAndPromptName, readAndPrintUpperCasedName);
        IOFunctions.toSafeValidation(program).run().on((IOException e) -> { e.printStackTrace(); return Unit.unit(); });
    }

    public static void ioExample2() {
        IOW.lift(IOFunctions.stdoutPrintln("What's your name again?"))
        .append(IOFunctions.stdoutPrint("Name: "))
        .append(IOFunctions.stdinReadLine())
        .bind(F1W.lift((String s) -> s.toUpperCase())
        .andThen(IOFunctions::stdoutPrintln))
        .safe().run().on((IOException e) -> { e.printStackTrace(); return Unit.unit(); });
    }

    public static void vectorExample() {
        F<Integer,Integer> square = x -> x * x;
        F<V2<Integer>, Integer> multiplyVectors = x -> x._1() * x._2();
        V3<Integer> vector1 = V.v(1, 2, 3);
        V3<Integer> vector2 = V.v(4, 5, 6);

        V3<V2<Integer>> zippedVector = vector1.vzip(vector2);
        V3<Integer> mappedVector = vector1.map(square);
        V3<Integer> mappedZippedVector = zippedVector.map(multiplyVectors);
        
        System.out.println(zippedVector._1()._2()); // prints 4
        System.out.println(mappedVector._3()); // prints 9
        System.out.println(mappedZippedVector._3()); // prints 18 (3 * 6)
        
    }

    public static void actorExample() {
        Effect1<String> effect = (x) -> System.out.println(x);
        Actor<String> actor = Actor.actor(Strategy.seqStrategy(), effect);


        F<String, String> costlyCalculation = (x) -> x.toUpperCase() + " from Thread ID: " + Thread.currentThread().getId();

        Promise.promise(Strategy.simpleThreadStrategy(), costlyCalculation).f("test").to(actor);
        Promise.promise(Strategy.simpleThreadStrategy(), costlyCalculation).f("test2").to(actor);
        Promise.promise(Strategy.simpleThreadStrategy(), costlyCalculation).f("test3").to(actor);

        actor.act("Simple Message to Actor");

        
    }

    public static void unitExample() {

        final fj.data.List<Unit> list = fj.data.List.list();
        F<Unit,Unit> unitfun = Unit -> Unit;
        list.map(unitfun);
        
        
    }

}
