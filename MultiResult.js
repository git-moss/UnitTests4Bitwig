// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

function MultiResult (resultOptions)
{
    this.options = resultOptions;
}

MultiResult.prototype.checkResult = function (result)
{
    for (var i = 0; i < this.options.length; i++)
    {
        if (this.options[i] == result)
            return true;
    }
    return false;
};

MultiResult.prototype.getDefaultValue = function ()
{
    return this.options[0];
}

MultiResult.prototype.printOptions = function ()
{
    return "{ " + this.options.join() + " }";
};
