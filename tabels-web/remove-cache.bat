REM This script remove ivy caches of maplab and tabels-core

RMDIR /S %USER_PROFILE\.ivy2\cache\es.ctic.tabels-tabels-core*
RMDIR /S %USER_PROFILE\.grails\ivy-cache\es.ctic.tabels
RMDIR /S %USER_PROFILE\.grails\ivy-cache\es.ctic.maplab
RMDIR /S %USER_PROFILE\.ivy2\cache\es.ctic.maplab\

