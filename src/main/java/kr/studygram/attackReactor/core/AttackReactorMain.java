package kr.studygram.attackReactor.core;

/**
 * Created by cynos07 on 2017-05-14.
 */

public class AttackReactorMain {
    public static void main(String[] args) {
        Thread attackListener = new Thread(AttackListener.getInstance());
        attackListener.start();
    }
}