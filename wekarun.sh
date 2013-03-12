#!/bin/bash
WEKALOC="$HOME/Applications/weka-3-7-9/weka.jar"
WEKA="java -Xmx1024m -classpath $WEKALOC"

cluster () {
    echo "Clustering..."
    $WEKA 
}

reduce () {
    echo "Reducing..."
}

creduce () {
    echo "Clustering the reduced data sets..."
}

nn_reduced () {
    echo "Training neural nets on reduced data..."
}

nn_clustered () {
    echo "Training neural nets on clustered data..."
}

usage () {
    echo "usage: $0 [cluster|reduce|cluster_reduced|nnet_reduced|nnet_cluster|all]\n"
    exit 1
}

if [[ $# -lt 1 ]]; then
    usage
fi

case $1 in
    cluster) 
        cluster
        ;;
    reduce) 
        reduce
        ;;
    cluster_reduce) 
        creduce
        ;;
    nnet_reduced) 
        nn_reduced
        ;;
    nnet_cluster) 
        nn_clustered
        ;;
    all) 
        cluster
        reduce
        creduce
        nn_reduced
        nn_clustered
        ;;
    *)  
        usage
        ;;
esac

