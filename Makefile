backup:
	test -d postgres-data && (rm -rf postgres-data-bkp && cp -r postgres-data postgres-data-bkp && echo "Backup created") || echo "No postgres-data found"
restore:
	test -d postgres-data-bkp && (rm -rf postgres-data && cp -r postgres-data-bkp postgres-data && echo "DB restored from backup") || echo "No postgres-data-bkp found"