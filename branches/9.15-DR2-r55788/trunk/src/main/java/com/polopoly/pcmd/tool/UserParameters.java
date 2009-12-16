package com.polopoly.pcmd.tool;

import java.util.ArrayList;
import java.util.List;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.argument.Parameters;
import com.polopoly.pcmd.parser.UserParser;
import com.polopoly.user.server.User;
import com.polopoly.util.client.PolopolyContext;

public class UserParameters implements Parameters {
    private List<User> users = new ArrayList<User>();

    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
        for (int i = 0; i < args.getArgumentCount(); i++) {
            UserParser userParser = new UserParser(context);
            addUser(args.getArgument(i, userParser));
        }
    }

    private void addUser(User user) {
        users.add(user);
    }

    public void getHelp(ParameterHelp help) {
        help.setArguments(new UserParser(), "The users to print information on (leave empty to list all).");
    }

    public List<User> getUsers() {
        return users;
    }
}
