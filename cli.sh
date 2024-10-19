format() {
    jq -c 'sort_by(.status, .id) | .[] | {id: .id, title: .title, status: .status}'
}

confirm() {
    # call with a prompt string or use a default
    read -r -p "${1:-Are you sure? [y/N]} " response
    case "$response" in
        [yY][eE][sS]|[yY])
            true
            ;;
        *)
            false
            ;;
    esac
}

get_task() {
  curl -s localhost:8080/api/tasks/$1 | jq '.'
}

list_all_tasks() {
  curl -s localhost:8080/api/tasks | format
}

list_open_tasks() {
	curl -s localhost:8080/api/tasks?status=Open | format
}

list_done_tasks() {
  curl -s localhost:8080/api/tasks?status=Done | format
}

add_task() {
  echo "Creating task:"
  status=${3:-Open}
  echo "{\"title\": \"$1\", \"description\": \"$2\", \"status\": \"$status\" }"
  if ! confirm "Proceed? [y/N]"; then return; fi

  curl -i -H 'Content-Type: application/json' -X POST --data "{\"title\": \"$1\", \"description\": \"$2\", \"status\": \"Open\" }" localhost:8080/api/tasks
}

update_status() {
  echo "Updating status of task with ID: $1. New status: $2"
  if ! confirm "Proceed? [y/N]"; then return; fi

  curl -i -H 'Content-Type: application/json' -X PATCH --data "{\"status\": \"$2\" }" localhost:8080/api/tasks/$1
}

close_task() {
  update_status $1 "Done"
}

help() {
  declare -F | sed 's/declare -f //' | grep -v '^confirm$' | grep -v '^format$'
}

"$@"