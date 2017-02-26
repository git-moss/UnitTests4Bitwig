// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

Logger.SEPARATOR_LINE = "----------------------------------------------------------------------";
Logger.PAD            = "  - ";


function Logger ()
{
}

Logger.prototype.equals = function (prefix, condition, expectedValue, actualValue)
{
    if (condition)
        this.info (prefix + " value should be '" + expectedValue + "', OK.");
    else
        this.error (prefix + " value should be '" + expectedValue + "' but was '" + actualValue + "'.");
};

Logger.prototype.finish = function (message)
{
    println (Logger.SEPARATOR_LINE);
    println ("Finished.");
};

Logger.prototype.infoLine = function ()
{
    println (Logger.SEPARATOR_LINE);
};

Logger.prototype.info = function (message)
{
    println (Logger.PAD + message);
};

Logger.prototype.error = function (message)
{
    host.errorln (Logger.PAD + message);
};
