using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using PAT.Common.Classes.Expressions.ExpressionClass;

//the namespace must be PAT.Lib, the class and method names can be arbitrary
namespace PAT.Lib
{    	  
    public class IndexURLTree : ExpressionValue
    {
        // List of files that have been created
        public List<int> fileList;
        // List of data that have been written into file 
        public List<int> dataList;

        // Needs a default constructor.
        public IndexURLTree() {
            this.fileList = new List<int>();
            this.dataList = new List<int>();
        }

        // tree: a list to be deep copied into the new IndexURLTree object.
        public IndexURLTree(List<int> fileList, List<int> dataList) {
            List<int> cloneFileList = new List<int>();
            
            foreach(int i in fileList) {
                cloneFileList.Add(i);
            }
            List<int> cloneDataList = new List<int>();
            
            foreach(int i in dataList) {
                cloneDataList.Add(i);
            }

            this.fileList = cloneFileList;
            this.dataList = cloneDataList;
        }

        public bool CreateFile(int url) {
            this.fileList.Add(url);
            return true;
        }

        public bool FileExists(int url) {
            return this.fileList.Contains(url);
        }

        public bool WriteData(int url) {
            this.dataList.Add(url);
            return true;
        }

        public bool FileListDuplicateExists() {
            var anyDuplicates = this.fileList.GroupBy(x => x).Any(g => g.Count() > 1);
            return anyDuplicates;
        }

        public bool DataListDuplicateExists() {
            var anyDuplicates = this.dataList.GroupBy(x => x).Any(g => g.Count() > 1);
            return anyDuplicates;
        }
        
        // Check if every value in array are both in fileList and dataList
        public bool ContainsAll(int[] array) {
        	foreach(int i in array) {
        		if (!this.fileList.Contains(i) || !this.dataList.Contains(i)) {
        			return false;
        		}
        	}
        	return true;
        }
        
        public bool CheckCorrectness(int[] array) {
        	if(this.fileList.Count() == this.dataList.Count()) {
        		if (this.fileList.Count() == array.Length) {
        			return ContainsAll(array) && !FileListDuplicateExists() && !DataListDuplicateExists();
        		}
        	}
        	return false;
        }

        //---------------------------- For system use ------------------------------
        /// Returns the  string representation of the datatype.
        public override string ToString() {
            return ExpressionID;
        }
        
        /// Returns a deep clone of the current object.
        public override ExpressionValue GetClone()
        {
            return new IndexURLTree(this.fileList, this.dataList);
        }
        
        /// Returns the compact string representation of the datatype.
        public override string ExpressionID
        {
            get {
                String result = "";
                
                
                result += "[FileList:[" + String.Join(", ", this.fileList.Select(i => i.ToString()).ToArray()) + "], ";
                
                
                
                return result; 
            }
        }
    }
}
