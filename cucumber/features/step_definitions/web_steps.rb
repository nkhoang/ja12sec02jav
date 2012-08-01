# Author: Hoang Nguyen
# Creation date: 07/30/2012
# Modified By: Hoang Nguyen
# Modification date: 07/30/2012

#====================================
# STATUS STEPS
#====================================
# Navigate to the specific URL
When /^I am on the "(.*)" page$/ do |page|
	visit("http://minion-1.appspot.com/vocabulary/" + page + ".html")
end

#====================================
# INTERACTION STEPS
#====================================

#====================================
# VERIFICATION STEPS
#====================================
Then /^The element with id "(.*)" should( not)? have focus$/ do |elementId, negate|
    if negate
        page.execute_script("document.activeElement['#{elementId}']") != elementId
    else
        page.execute_script("document.activeElement['#{elementId}']") == elementId
    end
end