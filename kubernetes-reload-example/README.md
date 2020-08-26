# Kubernetes Reload Example

This example demonstrate how to use the reload feature to change the configuration of a spring-boot application at runtime.

The application consists of a timed bean that periodically prints a message to the console. 
The message can be changed using a config map.

## Running the example

When using Openshift, you must assign at least the `view` role to the *default* service account in the current project:

```bash
oc policy add-role-to-user view --serviceaccount=default
```

### You can deploy the application using:

#### 1) with a Red Hat S2i imagem

```bash
oc new-app redhat-openjdk18-openshift:1.4~https://github.com/jovemfelix/spring-cloud-kubernetes.git \
	--context-dir=kubernetes-reload-example  \
	--name=kubernetes-reload-example \
	-lapp=kubernetes-reload-example
```

or

#### 2) the fabric8 maven plugin:

```bash
mvn clean install fabric8:build fabric8:deploy
```

### Changing the configuration

Create a yaml file with the following contents:

```yml
apiVersion: v1
kind: ConfigMap
metadata:
  name: kubernetes-reload-example
data:
  application.properties: |-
    spring.application.name=kubernetes-reload-example

    bean.message=Hello from OpenShift, Yet Again!
    spring.cloud.kubernetes.reload.enabled=true
    spring.cloud.kubernetes.reload.mode=polling
    spring.cloud.kubernetes.reload.period=100

    spring.cloud.kubernetes.reload.monitoring-config-maps=true
    spring.cloud.kubernetes.reload.monitoring-secrets=true
```

A sample config map is provided with this example in the *config-map.yml* file.

To deploy the config map, just run the following command on Openshift (just replace `oc` with `kubectl` if you are using plain Kubernetes):

```bash
oc apply -f src/k8s/configmap.yml
```

As soon as the config map is deployed, the output of the application changes accordingly.
The config map can be now edited with the following command:

```
oc edit configmap kubernetes-reload-example
```

Changes are applied immediately when using the *event* reload mode.

> The name of the config map (*"kubernetes-reload-example"*) should match with the name of the application (spring.application.name) as declared in the *application.properties* file.

## Reference

- Plugin Documentation - https://github.com/fabric8io/spring-cloud-kubernetes

- RBAC and Spring Cloud Kubernetes - https://medium.com/@nieldw/rbac-and-spring-cloud-kubernetes-847dd0f245e4

  - If you are using secret and have the error bellow:

    ```bash
    2020-08-26 13:32:34.656  WARN 1 --- [pool-1-thread-1] i.f.s.c.k.config.SecretsPropertySource   : Can't read secret with name: [app-config] or labels [{}] in namespace:[configmap] (cause: Failure executing: GET at: https://kubernetes.default.svc/api/v1/namespaces/configmap/secrets/app-config . Message: Forbidden!Configured service account doesn't have access. Service account may have been revoked..). Ignoring
    ```

    You have two options:

    1. You can change de ServiceAccount permission from `view` to `edit`;
    2. Or you can associante the ServiceAccount with the required minimum permission as shown bellow:

    

    > **Note**: If you are running in a Kubernetes environment where [RBAC](https://kubernetes.io/docs/reference/access-authn-authz/rbac/) is enabled, you need to make sure that your pod has the right level of authorizations to access the K8s APIs or resources. 
    >
    > To help you get started, a sample `ServiceAccount` and `RoleBinding` configuration is provided in `src/k8/sa.yaml` directory. These configuration needs to be applied to your K8s cluster and the newly created `ServiceAccount` needs to be attached to your pod spec like this:

    ```yaml
    spec:
      containers:
        image: <image_loc>
        imagePullPolicy: IfNotPresent
        livenessProbe:
          failureThreshold: 3
          httpGet:
            path: /actuator/health
            port: 8080
            scheme: HTTP
          initialDelaySeconds: 180
          successThreshold: 1
        name: spring-boot
        ports:
          - containerPort: 8080
            name: http
            protocol: TCP
          - containerPort: 9779
            name: prometheus
            protocol: TCP
        readinessProbe:
          failureThreshold: 3
          httpGet:
            path: /actuator/health
            port: 8080
            scheme: HTTP
          initialDelaySeconds: 10
          successThreshold: 1
        securityContext:
          privileged: false
      # here you put the <service_account_name>
      serviceAccountName: <service_account_name>
    ```

    