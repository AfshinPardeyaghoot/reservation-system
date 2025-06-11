local deleted = redis.call('ZREM', KEYS[1], ARGV[1])
return deleted