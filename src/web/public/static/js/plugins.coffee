###
# Console-lacking browsers.
# Should be removed for an app with a known environment.
###

# Avoid `console` errors in browsers that lack a console.
unless window.console and console.log
	do () ->
    noop = ->
    methods = ['assert', 'clear', 'count', 'debug', 'dir', 'dirxml', 'error', 'exception', 'group', 'groupCollapsed', 'groupEnd', 'info', 'log', 'markTimeline', 'profile', 'profileEnd', 'markTimeline', 'table', 'time', 'timeEnd', 'timeStamp', 'trace', 'warn']
    console = window.console = {}
    console[method] = noop for method in methods
    return


# Mix in non-conflict functions to Underscore namespace if you want
_.mixin _.str.exports()

# All functions, include conflict, will be available through _.str object
_.str.include 'Underscore.string', 'string'

console.log 'Plugins loaded'

