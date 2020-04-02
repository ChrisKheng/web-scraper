using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using PAT.Common.Classes.Expressions.ExpressionClass;

//the namespace must be PAT.Lib, the class and method names can be arbitrary
namespace PAT.Lib
{    	
    // Lists is a class which models an array of lists.
    // It can be used to model the collection of queues of crawler and 
    // the collection of buffers.        
    public class Lists : ExpressionValue
    {
        public List<int>[] lists;
        public int[] a;

        // Needs a default constructor.
        public Lists() {
            this.lists = new List<int>[0];
        }

        // numLists: number of lists in the array.
        public Lists(int numLists) {
            this.lists = new List<int>[numLists];

            // Initialise every element in the list
            for (int i = 0; i < numLists; i++) {
                this.lists[i] = new List<int>();
            }
        }

        // lists: an array of lists to be deep copied into the new Lists object.
        public Lists(List<int>[] lists) {
            List<int>[] clone = new List<int>[lists.Length];
            
            for (int i = 0; i < lists.Length; i++) {
                // copy the list and add it to the array
                clone[i] = lists[i].ToList();
            }
            
            this.lists = clone;
        }

        // Appends all the elements in the given array to the end of the 
        // target list.
        // listNumber: the index of the target list.        
        // Needs to return something for PAT code to work (i.e. cannot return void)
        public bool Append(int listNumber, int[] array) {
            this.lists[listNumber].AddRange(array);
			return true;
        }

        // Adds the given url into the target list.        
        // listNumber: the index of the target list. (index starts from 0)
        // url: the url to be added into the target list.        
        public void AddToList(int listNumber, int url) {
            lists[listNumber].Add(url);
        }
        
        // Removes and returns the first url from the target list.
        // listNumber: the index of the target list.
        public int PollFromList(int listNumber) {
            int head = lists[listNumber][0];
            lists[listNumber].RemoveAt(0);
            
            return head;
        }
        
        // Returns true if the target list is empty, otherwise false.
        // listNumber: the index of the target list.
        public bool isListEmpty(int listNumber) {
            return !(lists[listNumber].Any());
        }
                
        //---------------------------- For system use ------------------------------
        /// Returns the  string representation of the datatype.
        public override string ToString() {
            return ExpressionID;
        }
        
        /// Returns a deep clone of the current object.
        public override ExpressionValue GetClone()
        {
            return new Lists(this.lists);
        }
        
        /// Returns the compact string representation of the datatype.
        public override string ExpressionID
        {
            get {
                String result = "";
                
                foreach (List<int> list in this.lists) {
                    result += "[" + String.Join(", ", list) + "], ";
                }
                
               if (result.Length > 0) {
                   // Removes the ',' at the end and the trailing space.
                   result = result.Remove(result.Length - 2, 2);
               }
                
                return result; 
            }
        }
    }
}
