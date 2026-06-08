NAMESPACE=bankapp

.PHONY: bootstrap apply destroy help

help:
	@echo "Usage:"
	@echo "  make bootstrap  - Create namespace and configure basic settings"
	@echo "  make apply      - Apply all Kubernetes manifests to the cluster"
	@echo "  make destroy    - Delete all applied Kubernetes resources"

bootstrap:
	kubectl create namespace $(NAMESPACE) --dry-run=client -o yaml | kubectl apply -f -

apply:
	kubectl apply -f k8s/deployment.yaml -n $(NAMESPACE)
	kubectl apply -f k8s/gateway.yaml -n $(NAMESPACE)
	kubectl apply -f k8s/backup-cronjob.yaml -n $(NAMESPACE)
	kubectl apply -f k8s/argocd-app.yaml -n $(NAMESPACE)

destroy:
	kubectl delete -f k8s/argocd-app.yaml -n $(NAMESPACE) --ignore-not-found
	kubectl delete -f k8s/backup-cronjob.yaml -n $(NAMESPACE) --ignore-not-found
	kubectl delete -f k8s/gateway.yaml -n $(NAMESPACE) --ignore-not-found
	kubectl delete -f k8s/deployment.yaml -n $(NAMESPACE) --ignore-not-found
