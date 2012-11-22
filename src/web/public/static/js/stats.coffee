console.log 'Stats-side'

#
# This file generates all of the statistical measures.
#

$ ->
	# The mean is quite simple indeed.
	mean = (d) ->
		sum = _.reduce d, ((acc, v) -> acc + v), 0
		sum / d.length

	# Simplest (to code) median : sort, then pick the middle one.
	# Might not be the proper way to deal with even datasets.
	median = (d) ->
		sorted = _.sortBy d, (num) -> num;
		med = sorted[Math.floor sorted.length / 2]
		d.indexOf med

	# TODO : Bug. Scope bug ?
	variance = (tmpMean, d) ->
		squareSum = _.reduce d, ((acc, v) -> acc + v * v), 0
		(squareSum / d.length) - tmpMean * tmpMean

	# Pretty print of Strings
	# Uses Underscore.
	prettifyDim = (dim) ->
		if _.startsWith dim, 'http://' then _.humanize _.strRightBack dim, '/' else _.titleize _.swapCase dim

	# Pretty print of ints
	# Underscore again here.
	prettifyObs = (obs) ->
		_.numberFormat (_.toNumber Math.round obs), 0, decimalSeparator, thousandsSeparator

	# For each dataset.
	# Beware here, this is ugly. We might appreciate Knockout here.
	$('.observation-stats').each ->
		$this = $(@)
		index = $this.attr 'data-index'

		tmpMean = mean data[index][1]
		$('.stats' + index + '.stats-mean').html prettifyObs tmpMean

		medianIndex = median data[index][1]
		tmpMedian = data[index][1][medianIndex]
		tmpMedianDim = prettifyDim data[index][0][medianIndex]
		$('.stats' + index + '.stats-median').html (prettifyObs tmpMedian) + ' &mdash; ' + tmpMedianDim

		min = _.min data[index][1];
		minDim = prettifyDim data[index][0][data[index][1].indexOf min]
		$('.stats' + index + '.stats-min').html (prettifyObs min) + ' &mdash; ' + minDim

		max = _.max data[index][1];
		maxDim = prettifyDim data[index][0][data[index][1].indexOf max]
		$('.stats' + index + '.stats-max').html (prettifyObs max) + ' &mdash; ' + maxDim

		range = max - min
		$('.stats' + index + '.stats-range').html (prettifyObs range)

		tmpVar = variance tmpMean, data[index][1]
		$('.stats' + index + '.stats-var').html (prettifyObs tmpVar)

		tmpSd = Math.sqrt tmpVar
		$('.stats' + index + '.stats-sdev').html (prettifyObs tmpSd)

		# CoffeeScript "magic"
		return
