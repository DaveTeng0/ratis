package org.apache.ratis.shell.cli.sh.command;

import org.apache.commons.cli.CommandLine;

import java.io.IOException;

public abstract class BaseCommand
    extends AbstractRatisCommand
{
  protected BaseCommand(Context context) {
    super(context.getPrintStream());
  }

  public int run(CommandLine cl) throws IOException {
    return super.run(cl.getOptionValue(PEER_OPTION_NAME), cl.getOptionValue(GROUPID_OPTION_NAME));
  }

}
