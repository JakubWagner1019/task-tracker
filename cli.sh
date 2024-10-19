format() {
    jq -c 'sort_by(.status, .id) | .[] | {id: .id, title: .title, status: .status}'
}

list_all_tasks() {
  curl localhost:8080/api/tasks | format
}

list_open_tasks() {
	curl localhost:8080/api/tasks?status=Open | format
}

list_done_tasks() {
  curl localhost:8080/api/tasks?status=Done | format
}

add_task() {
  echo "Creating task:"
  status=${3:-Open}
  echo "{\"title\": \"$1\", \"description\": \"$2\", \"status\": \"$status\" }"
  curl -i -X POST --data "{\"title\": \"$1\", \"description\": \"$2\", \"status\": \"Open\" }" localhost:8080/api/tasks
}

help() {
  declare -F | sed 's/declare -f //'
}

"$@"