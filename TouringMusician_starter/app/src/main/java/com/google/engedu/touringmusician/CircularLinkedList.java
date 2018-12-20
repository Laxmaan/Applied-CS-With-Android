/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.touringmusician;


import android.graphics.Point;

import java.util.Iterator;

public class CircularLinkedList implements Iterable<Point> {

    private class Node {
        Point point;
        Node prev, next;

        Node(Point p){
            this.point=p;
        }
    }

    Node head;

    public void insertBeginning(Point p) {

        if(head==null){  //list empty
            head = new Node(p);
            head.next=head.prev=head;

        }
        else{
            Node newNode = new Node(p), lastNode=head.prev;  // keep track of last node, new node and first node (head)
            // set up new node connections;
            newNode.prev=lastNode;
            newNode.next=head;

            //overwrite other node connections
            head.prev=lastNode.next=newNode;

            //make head point to new node
            head = newNode;

        }

    }

    private float distanceBetween(Point from, Point to) {
        return (float) Math.sqrt(Math.pow(from.y-to.y, 2) + Math.pow(from.x-to.x, 2));
    }

    public float totalDistance() {
        float total = 0;
        boolean traversed = false;
        for(Node i=head;i!=null&&!traversed;i=i.next){
            if(i==head.prev)
                traversed=true;
            total+=distanceBetween(i.point,i.next.point);
        }

        return total;
    }

    public void insertNearest(Point p) {
            //list empty
        if(head==null)
            insertBeginning(p);
        else{
            Node newNode = new Node(p),closest=findNearest(p);
            insertAfter(closest,newNode);
        }
    }

    public void insertSmallest(Point p) {
        if(head==null)
            insertBeginning(p);
        else {
            Node A=head,B=head.next,insert_after=null;
            float min_gain=Float.MAX_VALUE;
            do{
                float gain = distanceBetween(A.point,p)+distanceBetween(B.point,p)
                                - distanceBetween(A.point,B.point);
                if(gain<min_gain){
                    min_gain=gain;
                    insert_after=A;
                }
                A=B;
                B=B.next;
            }while(A!=head);

            insertAfter(insert_after,new Node(p));
        }
    }

    public void reset() {
        head = null;
    }

    // HELPER METHODS

    void insertAfter(Node first, Node newNode){
        newNode.next = first.next;
        newNode.prev = first;
        first.next.prev=newNode;
        first.next=newNode;

    }

    Node findNearest(Point p){
        float min_dist=distanceBetween(p,head.point),dist;
        Node i = head.next, closest = head;
        while(i!=head){
            dist = distanceBetween(p,i.point);
            if(dist<min_dist){
                min_dist=dist;
                closest=i;
            }
            i=i.next;
        }
        return closest;
    }

    private class CircularLinkedListIterator implements Iterator<Point> {

        Node current;

        public CircularLinkedListIterator() {
            current = head;
        }

        @Override
        public boolean hasNext() {
            return (current != null);
        }

        @Override
        public Point next() {
            Point toReturn = current.point;
            current = current.next;
            if (current == head) {
                current = null;
            }
            return toReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Point> iterator() {
        return new CircularLinkedListIterator();
    }


}
