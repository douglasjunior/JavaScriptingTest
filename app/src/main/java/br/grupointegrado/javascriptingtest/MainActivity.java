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
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.IfStmt;
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
            String block = "{" +
                    "teste();\n" +
                    "for (int i = 0; i < 10.5; i++) { \n" +
                    "   teste2();\n" +
                    "}\n" +
                    "if (isTrue()) { \n" +
                    "   teste3();\n" +
                    "}\n" +
                    "invalido();" +
                    "}";

            BlockStmt b = JavaParser.parseBlock(block);
            System.out.println("Statments: " + b.getStmts().size());
            System.out.println("--------");
            executeBlock(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeBlock(BlockStmt b) {
        // percorre todas as instruções de código recebidas
        for (Statement st : b.getStmts()) {
            // se for do tipo Espressão
            if (st instanceof ExpressionStmt) {
                ExpressionStmt es = (ExpressionStmt) st;
                executeVoidMethod(es);
            }// se for um laço do tipo For
            else if (st instanceof ForStmt) {
                ForStmt fs = (ForStmt) st;
                executeFor(fs);
            }
            // se for uma condição do tipo If
            else if (st instanceof IfStmt) {
                IfStmt is = (IfStmt) st;
                executeIf(is);
            } else {
                throw new IllegalArgumentException("Expressão incrorreta, esperado chamada de método ou laço For: " + st);
            }
        }
    }

    private void executeIf(IfStmt is) {

    }

    private void executeFor(ForStmt fs) {
        // verifica o treço de comparação: for (int i = 0; <comparação> ; i++)
        if (fs.getCompare() instanceof BinaryExpr) {
            BinaryExpr compare = (BinaryExpr) fs.getCompare();
            // verifica se a comparação está no formato correto: for(int i = 0; i < [inteiro]; i++)
            if (compare.getRight() instanceof IntegerLiteralExpr) {
                IntegerLiteralExpr it = (IntegerLiteralExpr) compare.getRight();
                BinaryExpr.Operator op = compare.getOperator();
                // executa o corpo do For de acordo com a quantidade de vezes
                for (int i = 0; compareOperator(i, op, it); i++) {
                    if (fs.getBody() instanceof BlockStmt) {
                        executeBlock((BlockStmt) fs.getBody());
                    } else if (fs.getBody() instanceof ExpressionStmt) {
                        executeVoidMethod((ExpressionStmt) fs.getBody());
                    } else {
                        throw new IllegalArgumentException("Conteúdo do laço For incorreto: " + fs.getBody());
                    }
                }
            } else {
                throw new IllegalArgumentException("Comparação incrorreta, esperado número inteiro, recebido: " + compare.getRight());
            }
        } else {
            throw new IllegalArgumentException("Comparação incrorreta: " + fs.getCompare());
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

    private void executeVoidMethod(ExpressionStmt es) {
        if (es.getExpression() instanceof MethodCallExpr) {
            MethodCallExpr mt = (MethodCallExpr) es.getExpression();
            try {
                Method m = MainActivity.class.getDeclaredMethod(mt.getName());
                m.invoke(this);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Método não encontrado: " + mt);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Expressão incrorreta, esperado chamada de método, recebido: " + es.getExpression());
        }
    }

    public void teste() {
        System.out.println("chamou método teste()");
    }

    public void teste2() {
        System.out.println("chamou método teste2()");
    }

    public void teste3() {
        System.out.println("chamou método teste3()");
    }

    public boolean isTrue() {
        return true;
    }
}
