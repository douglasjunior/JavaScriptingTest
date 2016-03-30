package br.grupointegrado.javascriptingtest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.Statement;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        executarScript();
        Snackbar.make(v, "Executou!", Snackbar.LENGTH_LONG).show();
    }

    private void executarScript() {
        try {
            String clock = "{\n" +
                    "teste();\n" +
                    "for (int i = 0; i <= 10; i++)  \n" +
                    "   teste2();\n" +
                    "\n" +
                    "}";

            BlockStmt b = JavaParser.parseBlock(clock);
            System.out.println("Statments: " + b.getStmts().size());
            System.out.println("--------");
            executeBlock(b);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void executeBlock(BlockStmt b) {
        for (Statement st : b.getStmts()) {
            //System.out.println(st.getClass());
            if (st instanceof ExpressionStmt) {
                ExpressionStmt es = (ExpressionStmt) st;
                if (es.getExpression() instanceof MethodCallExpr) {
                    executeMethod((MethodCallExpr) es.getExpression());
                }
            } else if (st instanceof ForStmt) {
                ForStmt fs = (ForStmt) st;
                if (fs.getCompare() instanceof BinaryExpr) {
                    BinaryExpr compare = (BinaryExpr) fs.getCompare();
                    if (compare.getRight() instanceof IntegerLiteralExpr) {
                        IntegerLiteralExpr it = (IntegerLiteralExpr) compare.getRight();
                        BinaryExpr.Operator op = compare.getOperator();
                        for (int i = 0; compareOperator(i, op, it); i++) {
                            if (fs.getBody() instanceof BlockStmt) {
                                executeBlock((BlockStmt) fs.getBody());
                            } else if (fs.getBody() instanceof ExpressionStmt) {
                                ExpressionStmt es = (ExpressionStmt) fs.getBody();
                                if (es.getExpression() instanceof MethodCallExpr) {
                                    executeMethod((MethodCallExpr) es.getExpression());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean compareOperator(int i, BinaryExpr.Operator op, IntegerLiteralExpr it) {
        int value = Integer.parseInt(it.getValue());
        if (op.name().equals("less")) {
            return i < value;
        } else if (op.name().equals("lessEquals")) {
            return i <= value;
        }
        throw new IllegalArgumentException("Operador desconhecido: " + op);
    }

    private void executeMethod(MethodCallExpr mt) {
        try {
            Method m = MainActivity.class.getDeclaredMethod(mt.getName());
            m.invoke(this);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void teste() {
        System.out.println("chamou método test()");
    }

    public void teste2() {
        System.out.println("chamou método test2()");
    }
}
