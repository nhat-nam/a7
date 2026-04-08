package cs2110;

import cs2110.ast.Expression;
import java.util.Scanner;

/**
 * A calculator application that incorporates many of the assignment features.
 */
public class Calculator {

    /**
     * The most recent expression processed by the calculator, or `null` if no expression
     * has been entered yet.
     */
    Expression current;

    /**
     * Constructs a new calculator object that has not yet had an expression entered.
     */
    Calculator() {
        current = null;
    }

    /**
     * Returns whether `current` references a valid expression. Prints an error message if not.
     */
    public boolean currentSet() {
        if (current == null) { // haven't seen first expression yet
            System.out.println("You haven't entered an expression yet.");
            return false;
        }
        return true;
    }

    /**
     * Initiates the main loop of the calculator application, which prompts the user for a command
     * (read through the Scanner `in`) and then responds by delegating work to the correct
     * assignment method(s).
     */
    public void run(Scanner in) {
        System.out.println("Ready to Calculate!");
        System.out.println("The following commands are currently supported:");
        System.out.println(" - Enter any mathematical expression you'd like to work with.");
        System.out.println(" - \"tree\": visualize the previous expression as a tree");
        System.out.println(" - \"substitute <var>=<expr>\": replace all instances of <var> with <expr> in the previous expression, and simplify");
        System.out.println(" - \"simplify\": simplify the previous expression by constant folding");
        System.out.println(" - \"expand\": expand the previous expression by distributing all multiplications over additions");
        System.out.println(" - \"exit\": exit the calculator");

        System.out.print("> ");
        String line = in.nextLine();
        while (!line.equals("exit")) {
            if (line.equals("tree")) {
                if (currentSet()) {
                    System.out.println(current.treeString());
                }
            } else if (line.equals("simplify")) {
                if (currentSet()) {
                    current = current.simplify();
                    System.out.println(current.infixString());
                }
            } else if (line.equals("expand")) {
                if (currentSet()) {
                    current = current.expand();
                    System.out.println(current.infixString());
                }
            } else if (line.startsWith("substitute")) {
                if (currentSet()) {
                    char name = line.charAt(11);
                    if (name < 'a' || 'z' < name) {
                        System.out.println("Invalid variable name: " + name);
                        continue;
                    }
                    try {
                        Expression expr = ExpressionParser.parse(line.substring(13));
                        current = current.substitute(name, expr);
                        System.out.println(current.infixString());
                    } catch (MalformedExpression e) {
                        System.out.println("Malformed Expression: " + e.getMessage());
                    }
                }
            } else { // new expression
                try {
                    current = ExpressionParser.parse(line);
                    System.out.println(current.infixString());
                } catch (MalformedExpression e) {
                    System.out.println("Malformed Expression: " + e.getMessage());
                }
            }

            System.out.print("> ");
            line = in.nextLine();
        }
    }

    /**
     * Runs the calculator application, processing inputs from the console.
     */
    public static void main(String[] args) {
        try(Scanner in = new Scanner(System.in)) {
            new Calculator().run(in);
        }
    }
}
