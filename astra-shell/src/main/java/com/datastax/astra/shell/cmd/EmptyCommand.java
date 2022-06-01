package com.datastax.astra.shell.cmd;

import com.github.rvesse.airline.annotations.Command;

/**
 * Class to TODO
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "<empty>", description = "New prompt")
public class EmptyCommand implements Runnable {

    /** {@inheritDoc} */
    @Override
    public void run() {
        System.out.println(".");
    }
    

}
